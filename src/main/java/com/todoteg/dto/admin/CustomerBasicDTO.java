package com.todoteg.dto.admin;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CustomerBasicDTO {
    private Long userId;
    private String name;
    private String email;
    private String profileImage;
}