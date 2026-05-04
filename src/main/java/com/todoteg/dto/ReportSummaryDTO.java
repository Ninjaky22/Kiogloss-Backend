package com.todoteg.dto;

public class ReportSummaryDTO {

    private Double totalRevenue;
    private Long totalOrders;
    private Long activeProducts;
    private Double revenueToday;
    private Long ordersToday;

    public ReportSummaryDTO(Double totalRevenue, Long totalOrders, Long activeProducts,
                             Double revenueToday, Long ordersToday) {
        this.totalRevenue = totalRevenue;
        this.totalOrders = totalOrders;
        this.activeProducts = activeProducts;
        this.revenueToday = revenueToday;
        this.ordersToday = ordersToday;
    }

    public Double getTotalRevenue() { return totalRevenue; }
    public Long getTotalOrders() { return totalOrders; }
    public Long getActiveProducts() { return activeProducts; }
    public Double getRevenueToday() { return revenueToday; }
    public Long getOrdersToday() { return ordersToday; }
}
