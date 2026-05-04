package com.todoteg.dto;

import lombok.Setter;

public class OrderStatusDistributionDTO {

    private String status;
    private Long count;
    @Setter
    private Double percentage;

    public OrderStatusDistributionDTO(Object status, Long count) {
        this.status = status != null ? status.toString() : "UNKNOWN";
        this.count = count;
    }

    public String getStatus() { return status; }
    public Long getCount() { return count; }
    public Double getPercentage() { return percentage; }
}
