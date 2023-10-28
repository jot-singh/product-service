package com.dag.productservice.exceptionhandlers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.dag.productservice.exceptionhandlers.exceptions.NotFoundException;

@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ResponseErrorDto> handleNotFoundException(NotFoundException notFound){
        return new ResponseEntity<>(
                new ResponseErrorDto(notFound.getErrorMessage(), HttpStatus.NOT_FOUND),
                HttpStatus.NOT_FOUND);
    }

}