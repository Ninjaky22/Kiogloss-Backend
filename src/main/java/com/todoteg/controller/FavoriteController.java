package com.todoteg.controller;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.todoteg.dto.AccountFavoritesResponse;
import com.todoteg.dto.FavoriteCreateRequest;
import com.todoteg.service.FavoriteService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class FavoriteController {
    
    private final FavoriteService favoriteService;
    
    /**
     * POST /user/favorite/ - Agregar producto a favoritos
     */
    @PostMapping("/user/favorite")
    public ResponseEntity<Map<String, Long>> addFavorite(@Valid @RequestBody FavoriteCreateRequest request) {
        Long idFa = favoriteService.addFavorite(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("idFa", idFa));
    }
    
    /**
     * DELETE /user/delete/{pk}/ - Eliminar favorito
     */
    @DeleteMapping("/user/delete/{pk}")
    public ResponseEntity<Void> removeFavorite(@PathVariable Long pk) {
        favoriteService.removeFavorite(pk);
        return ResponseEntity.noContent().build();
    }
    
    /**
     * GET /user/account/account_favorites/{id}/ - Obtener favoritos de una cuenta
     */
    @GetMapping("/user/account/account_favorites/{id}")
    public ResponseEntity<AccountFavoritesResponse> getAccountFavorites(@PathVariable Long id) {
        AccountFavoritesResponse response = favoriteService.getAccountFavorites(id);
        return ResponseEntity.ok(response);
    }
}