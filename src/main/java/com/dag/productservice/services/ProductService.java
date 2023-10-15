package com.dag.productservice.services;

import com.dag.productservice.dto.schema.RequestDto;
import com.dag.productservice.dto.schema.ResponseDto;

public interface ProductService {
    ResponseDto getProductById(Long id);

    ResponseDto createProduct(RequestDto requestDto);

    ResponseDto[] getAllProducts();

    ResponseDto deleteproductById(Integer id);

    ResponseDto updateProductById(Long id, RequestDto requestDto);
}