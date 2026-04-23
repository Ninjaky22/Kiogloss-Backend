package com.todoteg.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VariantTypeDTO {
    private Long id;
    private String name;
    private String type;
    private List<VariantOptionDTO> options;
}
