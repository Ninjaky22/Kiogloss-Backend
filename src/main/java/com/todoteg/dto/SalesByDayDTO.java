package com.todoteg.dto;

import java.time.LocalDate;

public class SalesByDayDTO {

    private LocalDate date;
    private Double totalAmount;
    private Long totalOrders;

    public SalesByDayDTO(LocalDate date, Double totalAmount, Long totalOrders) {
        this.date = date;
        this.totalAmount = totalAmount;
        this.totalOrders = totalOrders;
    }

    public LocalDate getDate() { return date; }
    public Double getTotalAmount() { return totalAmount; }
    public Long getTotalOrders() { return totalOrders; }
}
