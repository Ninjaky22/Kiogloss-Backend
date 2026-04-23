package com.todoteg.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FavoriteProductItemDTO {
    private Long id;
    private String name;
    private String price;
    private String slug;
    private String image;
}