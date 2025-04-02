package com.authservice.exception;

import org.springframework.http.HttpStatus;

public class ValidationException extends BaseException {
    public static final String ERROR_CODE = "VAL-001";
    
    public ValidationException(String message) {
        super(message, HttpStatus.BAD_REQUEST, ERROR_CODE);
    }
    
    public ValidationException(String message, Throwable cause) {
        super(message, cause, HttpStatus.BAD_REQUEST, ERROR_CODE);
    }
}