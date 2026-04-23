package com.todoteg.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AccountFavoritesDTO {
    private Long id;
    private List<FavoriteIdDTO> favoriteID;
    private Object favorite; // puede ser lista o string
    private String address;
}