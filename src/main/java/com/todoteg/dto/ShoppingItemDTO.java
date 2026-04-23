package com.todoteg.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ShoppingItemDTO {
    private String title;
    private String slug;
    private String image;
    private String price;
    private Integer quantity;
    private String size;
    private String color;
    
    @JsonProperty("priceXquantity")
    private Double priceXquantity;
}