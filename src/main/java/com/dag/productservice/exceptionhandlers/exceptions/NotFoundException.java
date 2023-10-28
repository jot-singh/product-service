package com.dag.productservice.exceptionhandlers.exceptions;

import org.springframework.http.HttpStatus;

import lombok.Getter;

@Getter
public class NotFoundException extends RuntimeException{
    private String errorMessage;

    public NotFoundException(String errorMessage){
        this.errorMessage = errorMessage;
    }
}