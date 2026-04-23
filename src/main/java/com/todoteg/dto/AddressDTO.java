package com.todoteg.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AddressDTO {
    private Long id;
    private String street;
    private String streetNumber;
    private String distric;
    
    public String getFullAddress() {
        return street + " " + streetNumber + ", " + distric;
    }
}