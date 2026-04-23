package com.todoteg.exception;


import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.todoteg.dto.admin.ApiResponseDTO;

import jakarta.servlet.http.HttpServletResponse;

@RestControllerAdvice
public class GlobalExceptionHandler {
    
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiResponseDTO<Void>> handleDataIntegrityViolation(DataIntegrityViolationException ex, HttpServletResponse response) {
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        return ResponseEntity
            .status(HttpStatus.CONFLICT)
            .contentType(MediaType.APPLICATION_JSON)
            .body(ApiResponseDTO.error("No se puede eliminar este recurso porque está asociado a pedidos, favoritos u otros registros. Te recomendamos cambiar su estado a 'Borrador'."));
    }
    
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiResponseDTO<Void>> handleResourceNotFound(ResourceNotFoundException ex, HttpServletResponse response) {
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        return ResponseEntity
            .status(HttpStatus.NOT_FOUND)
            .contentType(MediaType.APPLICATION_JSON)
            .body(ApiResponseDTO.error(ex.getMessage()));
    }
    
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiResponseDTO<Void>> handleIllegalArgument(IllegalArgumentException ex, HttpServletResponse response) {
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .contentType(MediaType.APPLICATION_JSON)
            .body(ApiResponseDTO.error(ex.getMessage()));
    }
    
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiResponseDTO<Void>> handleAccessDenied(AccessDeniedException ex, HttpServletResponse response) {
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        return ResponseEntity
            .status(HttpStatus.FORBIDDEN)
            .contentType(MediaType.APPLICATION_JSON)
            .body(ApiResponseDTO.error("No tienes permisos para realizar esta acción"));
    }
    
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponseDTO<Void>> handleGeneral(Exception ex, HttpServletResponse response) {
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        return ResponseEntity
            .status(HttpStatus.INTERNAL_SERVER_ERROR)
            .contentType(MediaType.APPLICATION_JSON)
            .body(ApiResponseDTO.error("Error interno del servidor: " + ex));
    }
}