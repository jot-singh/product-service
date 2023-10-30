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

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ResponseErrorDto> handleIllegalArgumentException(IllegalArgumentException illegalArgument){
        return new ResponseEntity<>(
                new ResponseErrorDto(illegalArgument.getMessage(), HttpStatus.BAD_REQUEST),
                HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(NullPointerException.class)
    public ResponseEntity<ResponseErrorDto> handleNullPointerException(NullPointerException nullPointer){
        return new ResponseEntity<>(
                new ResponseErrorDto(nullPointer.getMessage(), HttpStatus.BAD_REQUEST),
                HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ResponseErrorDto> handleException(Exception exception){
        return new ResponseEntity<>(
                new ResponseErrorDto(exception.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR),
                HttpStatus.INTERNAL_SERVER_ERROR);
    }

}