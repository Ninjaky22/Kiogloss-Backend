package com.todoteg.controller;

import java.time.LocalDate;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import com.todoteg.dto.ErrorResponse;
import com.todoteg.dto.OrderCreateRequest;
import com.todoteg.dto.OrderDetailResponse;
import com.todoteg.model.OrderStatus;
import com.todoteg.model.UserProfile;
import com.todoteg.service.OrderService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class OrderController {
    
    private final OrderService orderService;
    
    /**
     * POST /user/order/ - Crear nueva orden
     */
    @PostMapping("/order")
    public ResponseEntity<Void> createOrder(@Valid @RequestBody OrderCreateRequest request) {
        orderService.createOrder(request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
    
    /**
     * GET /user/orders/ - Listar órdenes del usuario autenticado
     * Soporta filtros por fecha y estado
     */
    @GetMapping("/orders")
    public ResponseEntity<Page<OrderDetailResponse>> getUserOrders(
            Authentication authentication,
            @RequestParam(required = false) String statusOrder,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate rangeDate_after,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate rangeDate_before,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(name = "page_size", defaultValue = "8") int pageSize) {
        
        UserProfile user = (UserProfile) authentication.getPrincipal();
        Pageable pageable = PageRequest.of(page, pageSize);
        
        OrderStatus status = null;
        if (statusOrder != null && !statusOrder.isEmpty() && !statusOrder.equalsIgnoreCase("ALL")) {
            status = OrderStatus.fromString(statusOrder);
        }
        
        Page<OrderDetailResponse> orders = orderService.getUserOrders(
                user.getId(), status, rangeDate_after, rangeDate_before, pageable
        );
        
        return ResponseEntity.ok(orders);
    }
    
    /**
     * Manejo global de errores de formato de fecha
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponse> handleDateFormatException(
            MethodArgumentTypeMismatchException ex) {
        
        String message = "Formato de fecha inválido. Use el formato: YYYY-MM-DD (ejemplo: 2024-01-15)";
        return ResponseEntity.badRequest().body(new ErrorResponse(message));
    }
}