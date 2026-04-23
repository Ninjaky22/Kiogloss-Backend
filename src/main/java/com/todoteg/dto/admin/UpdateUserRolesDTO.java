package com.todoteg.dto.admin;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateUserRolesDTO {
    private Long userId;
    private Boolean isStaff;
    private Boolean isSuperuser;
    private Boolean isActive;
}