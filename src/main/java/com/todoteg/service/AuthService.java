package com.todoteg.service;


import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.todoteg.dto.LoginRequest;
import com.todoteg.dto.TokenResponse;
import com.todoteg.model.UserProfile;
import com.todoteg.repository.UserProfileRepository;
import com.todoteg.security.JwtUtil;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthService {
    
    private final AuthenticationManager authenticationManager;
    private final UserProfileRepository userRepository;
    private final JwtUtil jwtUtil;
    
    public TokenResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );
        
        UserProfile user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        
        String accessToken = jwtUtil.generateAccessToken(user, user.getId());
        String refreshToken = jwtUtil.generateRefreshToken(user, user.getId());
        
        return new TokenResponse(accessToken, refreshToken);
    }
    
    public TokenResponse refresh(String refreshToken) {
        String email = jwtUtil.extractUsername(refreshToken);
        UserProfile user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        
        if (!jwtUtil.validateToken(refreshToken, user)) {
            throw new RuntimeException("Invalid refresh token");
        }
        
        String newAccessToken = jwtUtil.generateAccessToken(user, user.getId());
        
        return new TokenResponse(newAccessToken, refreshToken);
    }
}