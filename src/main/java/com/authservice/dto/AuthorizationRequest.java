package com.authservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthorizationRequest {
    
    @NotNull(message = "Kullanıcı ID boş olamaz")
    private UUID userId;
    
    @NotBlank(message = "Nesne tipi boş olamaz")
    private String objectType;
    
    @NotBlank(message = "Nesne ID boş olamaz")
    private String objectId;
    
    @NotBlank(message = "İlişki boş olamaz")
    private String relation;
}
