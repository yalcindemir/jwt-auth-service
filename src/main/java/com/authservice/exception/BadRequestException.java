package com.authservice.exception;

import org.springframework.http.HttpStatus;

public class BadRequestException extends BaseException {
    public static final String ERROR_CODE = "REQ-001";
    
    public BadRequestException(String message) {
        super(message, HttpStatus.BAD_REQUEST, ERROR_CODE);
    }
    
    public BadRequestException(String message, Throwable cause) {
        super(message, cause, HttpStatus.BAD_REQUEST, ERROR_CODE);
    }
}