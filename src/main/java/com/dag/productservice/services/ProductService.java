package com.dag.productservice.services;

import com.dag.productservice.dto.ProductRequestDto;
import com.dag.productservice.dto.ProductResponseDto;

public interface ProductService {
    ProductResponseDto getProductById(Long id);

    ProductResponseDto createProduct(ProductRequestDto requestDto);

    ProductResponseDto[] getAllProducts();

    ProductResponseDto deleteproductById(Integer id);

    ProductResponseDto updateProductById(Long id, ProductRequestDto requestDto);
}