package com.authservice.exception;

import org.springframework.http.HttpStatus;

public class ResourceNotFoundException extends BaseException {
    public static final String ERROR_CODE = "RESOURCE-001";
    
    public ResourceNotFoundException(String message) {
        super(message, HttpStatus.NOT_FOUND, ERROR_CODE);
    }
    
    public ResourceNotFoundException(String message, Throwable cause) {
        super(message, cause, HttpStatus.NOT_FOUND, ERROR_CODE);
    }
}