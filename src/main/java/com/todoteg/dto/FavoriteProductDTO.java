package com.todoteg.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FavoriteProductDTO {
    @JsonProperty("idFa")
    private Long idFa;
    
    private Long id;
    private String name;
    private String price;
    private String slug;
    private List<String> images;
}