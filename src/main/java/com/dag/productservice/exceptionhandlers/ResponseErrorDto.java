package com.dag.productservice.exceptionhandlers;

import org.springframework.http.HttpStatus;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class ResponseErrorDto {
    private String errorMessage;
    private HttpStatus status;

    ResponseErrorDto(String errorMessage, HttpStatus httpStatus){
        this.errorMessage = errorMessage;
        this.status = httpStatus;
    }

}
