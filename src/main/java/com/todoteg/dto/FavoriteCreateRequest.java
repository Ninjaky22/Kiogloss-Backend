package com.todoteg.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FavoriteCreateRequest {
    @NotNull
    private Long account;
    
    @NotNull
    private Long product;
}

