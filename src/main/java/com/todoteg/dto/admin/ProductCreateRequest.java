package com.todoteg.dto.admin;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductCreateRequest {
    @NotBlank
    private String title;
    
    @NotNull
    private BigDecimal price;
    
    @NotBlank
    private String description;
    
    @NotBlank
    private String slug;
    
    private String status; // "draft" o "published"
    
    private Integer stock; // Used when no variants are provided (default variant)
    
    private Map<String, String> attributes;
    
    private List<ProductVariantRequestDTO> variants;
    private List<Long> tagIds;     // IDs de tags
}