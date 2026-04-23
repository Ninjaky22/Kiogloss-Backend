package com.todoteg.dto;

import java.util.List;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductDetailDTO {
    private Long id;
    private List<String> images;
    private String title;
    private String description;
    private List<String> tags;
    private String price;
    private Integer stock;
    private Map<String, String> attributes;
    private List<ProductVariantDTO> variants;
}