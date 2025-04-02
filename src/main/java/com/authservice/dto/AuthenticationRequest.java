package com.authservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthenticationRequest {
    
    @NotBlank(message = "Kullanıcı adı boş olamaz")
    private String username;
    
    @NotBlank(message = "Şifre boş olamaz")
    private String password;
    
    private String macAddress;
    
    private String deviceType;
    
    private String deviceName;
}
