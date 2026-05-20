package com.todoteg.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AccountInfoDTO {
    private Long id;
    private Object favorite; // puede ser lista o string
    
    @JsonProperty("pointsPerPurchase")
    private Integer pointsPerPurchase;
    
    private String address;
    private String city;

    @JsonProperty("isActive")
    private Boolean isActive;
}