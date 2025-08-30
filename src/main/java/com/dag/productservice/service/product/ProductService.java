package com.dag.productservice.service.product;

import com.dag.productservice.dto.ProductRequestDto;
import com.dag.productservice.dto.ProductResponseDto;

import java.util.List;

public interface ProductService {
    ProductResponseDto getProductById(String id);

    ProductResponseDto createProduct(ProductRequestDto requestDto);

    List<ProductResponseDto> getAllProducts();

    ProductResponseDto deleteProductById(String id); 

    ProductResponseDto updateProductById(String id, ProductRequestDto requestDto);
}