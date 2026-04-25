package com.todoteg.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserCreateRequest {
    @Email
    @NotBlank
    private String email;
    
    @NotBlank
    private String name;
    
    @NotBlank
    private String password;
    
    private String profileImage; // Base64
    
    private String phoneNumber;
    
    private AccountDTO account;
    
    private AddressDTO address;

    private Boolean isSuperuser = false;
}