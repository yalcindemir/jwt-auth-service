package com.authservice.exception;

import org.springframework.http.HttpStatus;

public class AuthorizationException extends BaseException {
    public static final String ERROR_CODE = "AUTH-002";
    
    public AuthorizationException(String message) {
        super(message, HttpStatus.FORBIDDEN, ERROR_CODE);
    }
    
    public AuthorizationException(String message, Throwable cause) {
        super(message, cause, HttpStatus.FORBIDDEN, ERROR_CODE);
    }
}