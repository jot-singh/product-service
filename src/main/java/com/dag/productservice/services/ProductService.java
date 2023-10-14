package com.dag.productservice.services;

import com.dag.productservice.dto.schema.ResponseDto;

public interface ProductService {
    abstract ResponseDto getProductById(Long id);
}