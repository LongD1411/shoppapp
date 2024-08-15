package com.project.shopapp.services;

import com.project.shopapp.dtos.ProductDTO;
import com.project.shopapp.dtos.ProductImageDTO;
import com.project.shopapp.entities.ProductImage;
import com.project.shopapp.exceptions.DataNotFoundException;
import com.project.shopapp.respone.ProductResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import com.project.shopapp.entities.Product;

import java.util.List;

public interface IProductService {
    Product createProduct(ProductDTO productDTO) throws Exception;
    ProductResponse getProduct(Long id) throws Exception;
    Page<ProductResponse> getAllProducts(String keyword, Long categoryId,PageRequest pageRequest);
    Product updateProduct(long id, ProductDTO productDTO) throws Exception;
    void deleteProduct(long id);
    boolean existsByName(String name);
    ProductImage createProductImage(Long productId, ProductImageDTO productImageDTO) throws Exception;
    List<ProductResponse> findAllProductsOrder(List<Long> ids);
}