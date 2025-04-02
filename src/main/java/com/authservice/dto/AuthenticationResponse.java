package com.authservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthenticationResponse {
    
    private String accessToken;
    
    private String refreshToken;
    
    private String tokenType;
    
    private Long expiresIn;
    
    private String userId;
    
    private String username;
}
