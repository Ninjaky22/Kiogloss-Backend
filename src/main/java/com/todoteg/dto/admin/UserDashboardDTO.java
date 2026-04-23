package com.todoteg.dto.admin;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDashboardDTO {
    private Long id;
    private String email;
    private String name;
    private String phoneNumber;
    private String profileImage;
    private Boolean isActive;
    private Boolean isStaff;
    private Boolean isSuperuser;

    private AccountSummaryDTO account;
    
    private Integer totalOrders;
    private Integer totalFavorites;
}