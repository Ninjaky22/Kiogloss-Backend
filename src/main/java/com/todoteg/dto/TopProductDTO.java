package com.todoteg.dto;

public class TopProductDTO {

    private Long productId;
    private String productTitle;
    private Long totalQuantitySold;
    private Double totalRevenue;

    public TopProductDTO(Long productId, String productTitle, Long totalQuantitySold, Double totalRevenue) {
        this.productId = productId;
        this.productTitle = productTitle;
        this.totalQuantitySold = totalQuantitySold;
        this.totalRevenue = totalRevenue;
    }

    public Long getProductId() { return productId; }
    public String getProductTitle() { return productTitle; }
    public Long getTotalQuantitySold() { return totalQuantitySold; }
    public Double getTotalRevenue() { return totalRevenue; }
}
