package com.authservice.exception;

import org.springframework.http.HttpStatus;

public class AuthenticationException extends BaseException {
    public static final String ERROR_CODE = "AUTH-001";
    
    public AuthenticationException(String message) {
        super(message, HttpStatus.UNAUTHORIZED, ERROR_CODE);
    }
    
    public AuthenticationException(String message, Throwable cause) {
        super(message, cause, HttpStatus.UNAUTHORIZED, ERROR_CODE);
    }
}