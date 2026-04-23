package com.todoteg.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductVariantDTO {
    private Long id;
    private List<VariantOptionDTO> options;
    private Integer stock;
    private String sku;
    private String imageUrl;
}