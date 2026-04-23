package com.todoteg.dto.admin;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductImageUploadRequest {
    @NotNull
    private Long productId;
    
    @NotBlank
    private String imageBase64; // Imagen en Base64
}