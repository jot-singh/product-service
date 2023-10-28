package com.dag.productservice.exceptionhandlers.exceptions;

import lombok.Getter;

@Getter
public class NotFoundException extends RuntimeException{
    private final String errorMessage;

    public NotFoundException(String errorMessage){
        this.errorMessage = errorMessage;
    }
}