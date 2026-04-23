package com.todoteg.dto.admin;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DashboardStatsDTO {
    private Long totalUsers;
    private Long activeUsers;
    private Long totalOrders;
    private Long pendingOrders;
    private Double totalRevenue;
    private Long newUsersThisMonth;
}