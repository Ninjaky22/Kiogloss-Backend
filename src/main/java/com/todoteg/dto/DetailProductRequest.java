package com.todoteg.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

//DTOs para Order
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DetailProductRequest {
 @NotNull
 private Long product;
 
 @NotNull
 private Integer quantity;
 
 @NotNull
 private Double price;
}