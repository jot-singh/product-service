package com.dag.productservice.services.product;

import com.dag.productservice.dto.ProductRequestDto;
import com.dag.productservice.dto.ProductResponseDto;

public interface ProductService {
    ProductResponseDto getProductById(String id);

    ProductResponseDto createProduct(ProductRequestDto requestDto);

    ProductResponseDto[] getAllProducts();

    ProductResponseDto deleteproductById(String id);

    ProductResponseDto updateProductById(String id, ProductRequestDto requestDto);
}