package com.todoteg.dto.admin;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderItemDTO {
    private Long id;
    private Integer quantity;
    private String size;
    private String color;
    private Double price;
    private ProductBasicDTO product;
}