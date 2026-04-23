package com.todoteg.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserUpdateRequest {
    private String name;
    private String password;
    private String profileImage; // Base64
    private String phoneNumber;
    private AddressDTO address;
}