package com.dag.productservice.exceptionhandlers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.dag.productservice.exceptionhandlers.dto.ResponseErrorDto;
import com.dag.productservice.exceptionhandlers.exceptions.NotFoundException;
import com.dag.productservice.exceptionhandlers.exceptions.RateLimitExceededException;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

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

    /**
     * Handle rate limit exceeded exceptions
     */
    @ExceptionHandler(RateLimitExceededException.class)
    public ResponseEntity<Map<String, Object>> handleRateLimitExceeded(RateLimitExceededException ex) {
        logger.warn("Rate limit exceeded: {}", ex.getMessage());

        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("timestamp", LocalDateTime.now());
        errorResponse.put("status", HttpStatus.TOO_MANY_REQUESTS.value());
        errorResponse.put("error", "Too Many Requests");
        errorResponse.put("message", ex.getMessage());
        errorResponse.put("retryAfterSeconds", ex.getRetryAfterSeconds());

        if (ex.getBucketKey() != null) {
            errorResponse.put("bucketKey", ex.getBucketKey());
        }

        return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
                           .header("Retry-After", String.valueOf(ex.getRetryAfterSeconds()))
                           .body(errorResponse);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ResponseErrorDto> handleException(Exception exception){
        logger.error("Unexpected error occurred", exception);
        return new ResponseEntity<>(
                new ResponseErrorDto(exception.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR),
                HttpStatus.INTERNAL_SERVER_ERROR);
    }
}