package com.dag.productservice.exception;

import lombok.Getter;

@Getter
public class NotFoundException extends RuntimeException{
    private final String errorMessage;

    public NotFoundException(String errorMessage){
        this.errorMessage = errorMessage;
    }
}