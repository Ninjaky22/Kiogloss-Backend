package com.todoteg.dto;

import java.time.LocalDate;
import java.util.List;

import com.todoteg.model.OrderStatus;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderDetailResponse {
    private Long id;
    private List<ShoppingItemDTO> shopping;
    private OrderUserDTO user;
    private Double amount;
    private LocalDate date;
    private OrderStatus status;
}