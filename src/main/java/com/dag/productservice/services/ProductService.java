package com.dag.productservice.services;

import org.springframework.http.ResponseEntity;

import com.dag.productservice.dto.schema.RequestDto;
import com.dag.productservice.dto.schema.ResponseDto;

public interface ProductService {
    ResponseDto getProductById(Long id);

    ResponseDto createProduct(RequestDto requestDto);

    ResponseEntity<ResponseDto[]> getAllProducts();

    ResponseDto deleteproductById(Integer id);
}