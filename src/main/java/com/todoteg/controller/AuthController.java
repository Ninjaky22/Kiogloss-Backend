package com.todoteg.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.todoteg.dto.LoginRequest;
import com.todoteg.dto.RefreshTokenRequest;
import com.todoteg.dto.TokenResponse;
import com.todoteg.service.AuthService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class AuthController {
    
    private final AuthService authService;
    
    /**
     * POST /login - Genera access y refresh tokens
     */
    @PostMapping("/login")
    public ResponseEntity<TokenResponse> login(@Valid @RequestBody LoginRequest request) {
        TokenResponse response = authService.login(request);
        return ResponseEntity.ok(response);
    }
    
    /**
     * POST /refresh - Genera nuevo access token a partir del refresh token
     */
    @PostMapping("/refresh")
    public ResponseEntity<TokenResponse> refresh(@Valid @RequestBody RefreshTokenRequest request) {
        TokenResponse response = authService.refresh(request.getRefresh());
        return ResponseEntity.ok(response);
    }
}