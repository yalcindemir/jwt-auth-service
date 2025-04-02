package com.authservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeviceRegistrationRequest {
    
    @NotNull(message = "Kullanıcı ID boş olamaz")
    private UUID userId;
    
    @NotBlank(message = "MAC adresi boş olamaz")
    private String macAddress;
    
    private String deviceType;
    
    private String deviceName;
}
