package com.todoteg.controller;

import com.todoteg.dto.*;
import com.todoteg.service.PasswordResetService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.todoteg.service.AuthService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import java.util.Map;

@RestController
@RequiredArgsConstructor
public class AuthController {
    
    private final AuthService authService;
    private final PasswordResetService passwordResetService;
    
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

    /**
     * POST /auth/forgot-password
     * Body: { "email": "usuario@email.com" }
     * Siempre responde 200 para no revelar si el email existe.
     */
    @PostMapping("/auth/forgot-password")
    public ResponseEntity<Map<String, String>> forgotPassword(
            @Valid @RequestBody ForgotPasswordRequest request) {
        passwordResetService.requestReset(request.getEmail());
        return ResponseEntity.ok(Map.of(
                "message", "Si el email existe, recibirás un enlace para restablecer tu contraseña"
        ));
    }

    /**
     * POST /auth/reset-password
     * Body: { "token": "uuid-del-email", "newPassword": "nuevaClave123" }
     */
    @PostMapping("/auth/reset-password")
    public ResponseEntity<Map<String, String>> resetPassword(
            @Valid @RequestBody ResetPasswordRequest request) {
        passwordResetService.confirmReset(request.getToken(), request.getNewPassword());
        return ResponseEntity.ok(Map.of(
                "message", "Contraseña restablecida exitosamente"
        ));
    }
}