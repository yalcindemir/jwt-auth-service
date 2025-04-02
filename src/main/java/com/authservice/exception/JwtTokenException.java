package com.authservice.exception;

import org.springframework.http.HttpStatus;

public class JwtTokenException extends BaseException {
    public static final String ERROR_CODE = "JWT-001";
    
    public JwtTokenException(String message) {
        super(message, HttpStatus.UNAUTHORIZED, ERROR_CODE);
    }
    
    public JwtTokenException(String message, Throwable cause) {
        super(message, cause, HttpStatus.UNAUTHORIZED, ERROR_CODE);
    }
}