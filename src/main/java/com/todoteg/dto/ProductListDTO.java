package com.todoteg.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductListDTO {
    private Long id;
    private String image;
    private String title;
    private String slug;
    private String price;
    private Integer stock;
    private boolean hasVariants;
    private Long defaultVariantId; // ID of the single default variant (for direct add-to-cart when !hasVariants)
}