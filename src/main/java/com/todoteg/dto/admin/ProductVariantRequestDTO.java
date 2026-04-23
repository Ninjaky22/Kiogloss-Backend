package com.todoteg.dto.admin;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductVariantRequestDTO {
    private Long id; // Optional for creation, required for update
    private List<Long> optionIds;
    private Integer stock;
    private String sku;
    private String imageUrl;
}