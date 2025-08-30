package com.dag.productservice.dto;

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

    public ResponseErrorDto(String errorMessage, HttpStatus httpStatus){
        this.errorMessage = errorMessage;
        this.status = httpStatus;
    }

}
