package com.todoteg.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDetailResponse {
    private Long id;
    private String name;
    private String email;
    private String profileImage;
    private String phoneNumber;
    private AccountInfoDTO account;
}