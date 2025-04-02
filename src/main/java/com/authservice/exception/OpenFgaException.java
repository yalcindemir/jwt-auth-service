package com.authservice.exception;

/**
 * Custom exception for OpenFGA related errors.
 */
public class OpenFgaException extends RuntimeException {

    public OpenFgaException(String message) {
        super(message);
    }

    public OpenFgaException(String message, Throwable cause) {
        super(message, cause);
    }
}