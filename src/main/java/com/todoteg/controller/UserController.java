package com.todoteg.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.todoteg.dto.ErrorResponse;
import com.todoteg.dto.LoginRequest;
import com.todoteg.dto.TokenResponse;
import com.todoteg.dto.UserCreateRequest;
import com.todoteg.dto.UserDetailResponse;
import com.todoteg.dto.UserUpdateRequest;
import com.todoteg.model.UserProfile;
import com.todoteg.security.JwtUtil;
import com.todoteg.service.AuthService;
import com.todoteg.service.UserService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {
    
    private final UserService userService;
    private final AuthService authService;
    private final JwtUtil jwtUtil;
    
    
    @GetMapping("/")
    public ResponseEntity<Page<UserProfile>> findAllUsers(
    		@RequestParam(required = false) String search,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(name = "page_size", defaultValue = "8") int pageSize
    		){
    	Pageable pageable = PageRequest.of(page, pageSize, Sort.by("name").descending());
    	return ResponseEntity.ok(userService.findAllUsers(pageable));
    }
    
    /**
     * POST /user/ - Crear nuevo usuario y retornar tokens JWT
     */
    @PostMapping("/")
    public ResponseEntity<TokenResponse> createUser(
            @Valid @RequestBody UserCreateRequest request) throws Exception {
        
        userService.createUser(request);
        
        LoginRequest loginRequest = new LoginRequest(request.getEmail(), request.getPassword());
        TokenResponse tokens = authService.login(loginRequest);
        
        return ResponseEntity.status(HttpStatus.CREATED).body(tokens);
    }
    
    /**
     * GET /user/{pk}/ - Obtener detalle del usuario autenticado
     * Valida que el token corresponda al usuario solicitado
     */
    @GetMapping("/{pk}")
    public ResponseEntity<?> getUserDetail(
            @PathVariable Long pk,
            HttpServletRequest request,
            Authentication authentication) {
        
        // Extraer token y validar user_id
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            Long tokenUserId = jwtUtil.extractUserId(token);
            
            // Validar que el usuario del token coincida con el solicitado
            if (!tokenUserId.equals(pk)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(new ErrorResponse("Acceso no autorizado"));
            }
        }
        
        UserDetailResponse response = userService.getUserDetail(pk, request.getRequestURL().toString());
        return ResponseEntity.ok(response);
    }
    
    /**
     * PATCH /user/update/{pk}/ - Actualizar usuario parcialmente
     */
    @PatchMapping("/update/{pk}")
    public ResponseEntity<Void> updateUser(
            @PathVariable Long pk,
            @Valid @RequestBody UserUpdateRequest request,
            Authentication authentication) throws Exception {
        
        userService.updateUser(pk, request);
        return ResponseEntity.noContent().build();
    }
}

