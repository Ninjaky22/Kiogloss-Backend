package com.todoteg.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VariantOptionDTO {
    private Long id;
    private String typeName; // e.g. "Color", "Talla", "Modelo"
    private String value;
    private String metaValue;
}
