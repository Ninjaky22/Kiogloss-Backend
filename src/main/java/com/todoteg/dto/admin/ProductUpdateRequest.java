package com.todoteg.dto.admin;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductUpdateRequest {
    private String title;
    private BigDecimal price;
    private String description;
    private String slug;
    private String status;
    private Integer stock; // Used when no variants are provided (default variant)
    private Map<String, String> attributes;
    private List<ProductVariantRequestDTO> variants;
    private List<Long> tagIds;
}