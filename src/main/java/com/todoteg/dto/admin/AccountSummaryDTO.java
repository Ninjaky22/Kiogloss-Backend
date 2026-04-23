package com.todoteg.dto.admin;

import com.todoteg.dto.AddressDTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AccountSummaryDTO {
    private Long id;
    private Integer pointsPerPurchase;
    private Boolean isActive;
    private AddressDTO address;
}