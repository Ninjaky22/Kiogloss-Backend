package com.todoteg.dto.admin;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDetailDTO {
    private Long id;
    private String email;
    private String name;
    private String phoneNumber;
    private String profileImage;
    private Boolean isActive;
    private Boolean isStaff;
    private Boolean isSuperuser;
    
    // Cuenta con más detalles
    private AccountDetailDTO account;
    
    // Lista de órdenes recientes
    private List<OrderSummaryDTO> recentOrders;
}