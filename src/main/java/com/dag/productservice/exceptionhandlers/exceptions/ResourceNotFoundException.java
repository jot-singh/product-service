package com.dag.productservice.exceptionhandlers.exceptions;

public class ResourceNotFoundException extends RuntimeException{
    public ResourceNotFoundException(String exception) {
        super(exception);
    }

}
