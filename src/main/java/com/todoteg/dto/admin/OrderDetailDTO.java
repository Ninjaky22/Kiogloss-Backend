package com.todoteg.dto.admin;

import java.time.LocalDate;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderDetailDTO {
    private Long id;
    private Double amount;
    private LocalDate date;
    private String status;
    
    // Cliente que realizó la orden
    private CustomerBasicDTO customer;
    
    // Productos en la orden
    private List<OrderItemDTO> items;
}