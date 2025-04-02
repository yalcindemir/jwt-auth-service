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
