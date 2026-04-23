package com.todoteg.dto.admin;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VariantOptionCreateRequest {
    private Long id; // null for new options, present for existing ones (used in update)
    private String value;
    private String metaValue;
}
