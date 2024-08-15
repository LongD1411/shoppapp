package com.project.shopapp.services.impls;

import com.project.shopapp.componens.JwtTokenUtils;
import com.project.shopapp.dtos.UserDTO;
import com.project.shopapp.entities.Role;
import com.project.shopapp.entities.User;
import com.project.shopapp.exceptions.DataNotFoundException;
import com.project.shopapp.exceptions.PermissionDeniedDataAccessException;
import com.project.shopapp.repositories.RoleRepository;
import com.project.shopapp.repositories.UserRepository;
import com.project.shopapp.respone.UserResponse;
import com.project.shopapp.services.IUserService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;
@Service
@RequiredArgsConstructor
public class UserService implements IUserService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenUtils jwtTokenUtil;
    private  final AuthenticationManager authenticationManager;
    private  final ModelMapper modelMapper;
    @Override

    // register user
    public User createUser(UserDTO userDTO) throws Exception {
        String phoneNumber = userDTO.getPhoneNumber();
        if (userRepository.existsByPhoneNumber(phoneNumber)) {
            throw new DataIntegrityViolationException("Phone number already exists");
        }
        User newUser = User.builder()
                .fullName(userDTO.getFullName())
                .phoneNumber(userDTO.getPhoneNumber())
                .password(userDTO.getPassword())
                .address(userDTO.getAddress())
                .dateOfBirth(userDTO.getDateOfBirth())
                .facebookAccountId(userDTO.getFacebookAccountId())
                .googleAccountId(userDTO.getGoogleAccountId())
                .build();
        Role role = roleRepository.findById(userDTO.getRoleId())
                .orElseThrow(() -> new DataNotFoundException("Role not found"));
        if(role.getName().equalsIgnoreCase("ADMIN")){
            throw new PermissionDeniedDataAccessException("You can't register admin account");
        }
        newUser.setRole(role);
        if (userDTO.getFacebookAccountId() == 0 && userDTO.getGoogleAccountId() == 0) {
            String password = userDTO.getPassword();
            String encodedPassword = passwordEncoder.encode(password);
            newUser.setPassword(encodedPassword);
        }
        return userRepository.save(newUser);
    }

    @Override
    public String login(String phoneNumber, String password) throws Exception {
        Optional<User> optionalUser = userRepository.findByPhoneNumber(phoneNumber);
        if(optionalUser.isEmpty()){
            throw new DataNotFoundException("Invalid phone number or password");
        }
        User existingUser = optionalUser.get();
        if (existingUser.getFacebookAccountId() == 0 && existingUser.getGoogleAccountId() == 0) {
            if(!passwordEncoder.matches(password,existingUser.getPassword())){
                throw new BadCredentialsException("Wrong phone number or password");
            }
        }
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                phoneNumber, password,existingUser.getAuthorities()
        );
        authenticationManager.authenticate(authenticationToken);
        return jwtTokenUtil.generateToken(existingUser);
    }

    @Override
    public UserResponse getUserByToken(String token) {
        String phoneNumber = jwtTokenUtil.extractPhoneNumber(token);
        User user = userRepository.findByPhoneNumber(phoneNumber).orElseThrow(
                ()-> new UsernameNotFoundException("Can't login with phone number" + phoneNumber));
        UserResponse userResponse = new UserResponse();
        modelMapper.typeMap(User.class, UserResponse.class).map(user,userResponse);
        return userResponse;
    }
}