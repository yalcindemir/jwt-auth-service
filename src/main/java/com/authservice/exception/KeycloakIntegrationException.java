package com.authservice.exception;

import org.springframework.http.HttpStatus;

public class KeycloakIntegrationException extends BaseException {
    public static final String ERROR_CODE = "KEYCLOAK-001";
    
    public KeycloakIntegrationException(String message) {
        super(message, HttpStatus.INTERNAL_SERVER_ERROR, ERROR_CODE);
    }
    
    public KeycloakIntegrationException(String message, Throwable cause) {
        super(message, cause, HttpStatus.INTERNAL_SERVER_ERROR, ERROR_CODE);
    }
}