package com.todoteg.mapper;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import com.todoteg.dto.admin.OrderSummaryDTO;
import com.todoteg.dto.admin.PageResponseDTO;
import com.todoteg.model.Order;

@Component
public class OrderMapper {
    
    private final UserMapper userMapper;
    
    public OrderMapper(UserMapper userMapper) {
        this.userMapper = userMapper;
    }
    
    /**
     * Convierte Page de Order a PageResponseDTO
     */
    public PageResponseDTO<OrderSummaryDTO> toOrderSummaryPage(Page<Order> page) {
        List<OrderSummaryDTO> content = page.getContent().stream()
            .map(userMapper::toOrderSummaryDTO)
            .collect(Collectors.toList());
        
        return new PageResponseDTO<>(
            content,
            page.getNumber(),
            page.getSize(),
            page.getTotalElements(),
            page.getTotalPages(),
            page.isLast()
        );
    }
}