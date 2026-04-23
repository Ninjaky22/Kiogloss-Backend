package com.todoteg.dto;

import java.util.List;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderCreateRequest {
    @NotNull
    private Long account;
    
    @NotNull
    private List<DetailProductRequest> shopping;
    
    @NotNull
    private Double amount;
    
    @NotNull
    private String status;
}