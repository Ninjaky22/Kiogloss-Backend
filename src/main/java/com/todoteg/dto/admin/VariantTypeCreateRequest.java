package com.todoteg.dto.admin;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VariantTypeCreateRequest {
    private String name;
    private String type; // e.g. "button", "color"
    private List<VariantOptionCreateRequest> options;
}
