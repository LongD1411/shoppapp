package com.project.shopapp.configurations;

import com.project.shopapp.filters.JwtTokenFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.CorsConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

import static org.springframework.http.HttpMethod.*;

@Configuration
@EnableMethodSecurity
@RequiredArgsConstructor
public class WebSecurityConfig {
    private final JwtTokenFilter jwtTokenFilter;
    @Value("${api.prefix}")
    private String apiPrefix;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .addFilterBefore(jwtTokenFilter, UsernamePasswordAuthenticationFilter.class)
                .authorizeHttpRequests(request -> {
                    request.requestMatchers(String.format("%s/users/register", apiPrefix),
                                            String.format("%s/users/login", apiPrefix)).permitAll()
                            .requestMatchers(GET,  String.format("%s/auth/**", apiPrefix)).permitAll()
                            .requestMatchers(GET,  String.format("%s/resources/**", apiPrefix)).permitAll()
                            .requestMatchers(GET, String.format("%s/products/images/*", apiPrefix)).permitAll()
                            .requestMatchers(GET, String.format("%s/banner/images/*", apiPrefix)).permitAll()
                            .requestMatchers(GET, String.format("%s/size", apiPrefix)).permitAll()
                            .requestMatchers(GET, String.format("%s/size/**", apiPrefix)).permitAll()
                            .requestMatchers(GET, String.format("%s/products/orders", apiPrefix)).permitAll()
                            .requestMatchers(GET, String.format("%s/products", apiPrefix)).permitAll()
                            .requestMatchers(GET, String.format("%s/products/**", apiPrefix)).permitAll()
                            .requestMatchers(GET, String.format("%s/banner", apiPrefix)).permitAll()
                            .requestMatchers(GET, String.format("%s/orders/**", apiPrefix)).permitAll()
                            .requestMatchers(GET, String.format("%s/categories**", apiPrefix)).permitAll()
                            .requestMatchers(String.format("%s/size", apiPrefix)).hasAnyRole("ADMIN")
                            .requestMatchers(String.format("%s/banner", apiPrefix)).hasAnyRole("ADMIN")
                            .requestMatchers(DELETE, String.format("%s/size/delete", apiPrefix)).hasAnyRole("ADMIN")
                            .requestMatchers(DELETE, String.format("%s/products/**", apiPrefix)).hasAnyRole("ADMIN")
                            .requestMatchers(PUT, String.format("%s/products", apiPrefix)).hasAnyRole("ADMIN")
                            .requestMatchers(POST, String.format("%s/products/**", apiPrefix)).hasAnyRole("ADMIN")
                            .requestMatchers(DELETE, String.format("%s/categories/**", apiPrefix)).hasAnyRole("ADMIN")
                            .requestMatchers(PUT, String.format("%s/categories", apiPrefix)).hasAnyRole("ADMIN")
                            .requestMatchers(POST, String.format("%s/categories/**", apiPrefix)).hasAnyRole("ADMIN")
                            .requestMatchers(POST, String.format("%s/orders", apiPrefix)).hasAnyRole("USER", "ADMIN")
                            .requestMatchers(PUT, String.format("%s/orders/**", apiPrefix)).hasRole("ADMIN")
                            .requestMatchers(DELETE, String.format("%s/orders/**", apiPrefix)).hasRole("ADMIN")
                            .requestMatchers(POST, String.format("%s/order-details/**", apiPrefix)).hasAnyRole("USER")
                            .requestMatchers(GET, String.format("%s/order-details/**", apiPrefix)).hasAnyRole("ADMIN", "USER")
                            .requestMatchers(PUT, String.format("%s/order-details/**", apiPrefix)).hasRole("ADMIN")
                            .requestMatchers(DELETE, String.format("%s/order-details/**", apiPrefix)).hasRole("ADMIN")
                            .anyRequest().authenticated();
                });
        http.cors(new Customizer<CorsConfigurer<HttpSecurity>>() {
            @Override
            public void customize(CorsConfigurer<HttpSecurity> httpSecurityCorsConfigurer) {
                CorsConfiguration configuration = new CorsConfiguration();
                configuration.setAllowedOrigins(List.of("*"));
                configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));
                configuration.setAllowedHeaders(Arrays.asList("authorization", "content-type", "x-auth-token"));
                configuration.setExposedHeaders(List.of("x-auth-token"));
                UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
                source.registerCorsConfiguration("/**", configuration);
                httpSecurityCorsConfigurer.configurationSource(source);
            }
        });
        return http.build();
    }
}
