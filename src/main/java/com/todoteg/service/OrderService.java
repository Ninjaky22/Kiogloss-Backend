package com.todoteg.service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.todoteg.dto.DetailProductRequest;
import com.todoteg.dto.OrderCreateRequest;
import com.todoteg.dto.OrderDetailResponse;
import com.todoteg.dto.OrderUserDTO;
import com.todoteg.dto.ShoppingItemDTO;
import com.todoteg.exception.ResourceNotFoundException;
import com.todoteg.model.Account;
import com.todoteg.model.DetailOrder;
import com.todoteg.model.Order;
import com.todoteg.model.OrderStatus;
import com.todoteg.model.Product;
import com.todoteg.model.UserProfile;
import com.todoteg.repository.AccountRepository;
import com.todoteg.repository.DetailOrderRepository;
import com.todoteg.repository.OrderRepository;
import com.todoteg.repository.ProductRepository;
import com.todoteg.repository.UserProfileRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OrderService {
    
    private final OrderRepository orderRepository;
    private final DetailOrderRepository detailOrderRepository;
    private final AccountRepository accountRepository;
    private final ProductRepository productRepository;
    private final com.todoteg.repository.ProductVariantRepository variantRepository;
    private final UserProfileRepository userRepository;
    private final CloudinaryService cloudinaryService;
    private final NotificationService notificationService;

    @Transactional
    public void createOrder(OrderCreateRequest request) {
        Account account = accountRepository.findById(request.getAccount())
                .orElseThrow(() -> new RuntimeException("Account not found"));
        
        Order order = new Order();
        order.setAccount(account);
        order.setAmount(request.getAmount());
        order.setStatus(OrderStatus.valueOf(request.getStatus().toUpperCase()));
        order.setDate(LocalDate.now());
        
        order = orderRepository.save(order);

        try {
            notificationService.notifyOrderCreated(order);
        } catch (Exception e) {
            // best-effort
        }

        // Crear detalles de orden
        for (DetailProductRequest detail : request.getShopping()) {
            com.todoteg.model.ProductVariant variant = variantRepository.findById(detail.getProduct())
                    .orElseThrow(() -> new RuntimeException("Variant not found"));
            
            if (variant.getStock() < detail.getQuantity()) {
                throw new RuntimeException(
                    "Stock insuficiente para el producto: " + variant.getProduct().getTitle() + 
                    ". Disponible: " + variant.getStock() + 
                    ", Solicitado: " + detail.getQuantity()
                );
            }
            
            // Actualizar stock de la variante
            variant.setStock(variant.getStock() - detail.getQuantity());
            variantRepository.save(variant);

            try {
                notificationService.checkAndNotifyStock(variant);
            } catch (Exception e) {
                // best-effort: no afecta la creación de la orden
            }
            
            DetailOrder detailOrder = new DetailOrder();
            detailOrder.setOrder(order);
            detailOrder.setVariant(variant);
            detailOrder.setQuantity(detail.getQuantity());
            detailOrder.setPrice(detail.getPrice());
            
            detailOrderRepository.save(detailOrder);
        }
    }
    
    public Page<OrderDetailResponse> getUserOrders(Long userId, OrderStatus status, 
                                                    LocalDate startDate, LocalDate endDate, 
                                                    Pageable pageable) {
        Account account = accountRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Account not found"));
        
        Page<Order> orders = orderRepository.findByAccountWithFilters(
                account.getId(), status, startDate, endDate, pageable
        );
        
        return orders.map(this::convertToOrderDetailResponse);
    }
    
    private OrderDetailResponse convertToOrderDetailResponse(Order order) {
        List<DetailOrder> details = detailOrderRepository.findByOrderId(order.getId());
        
        List<ShoppingItemDTO> shoppingItems = details.stream().map(detail -> {
            ShoppingItemDTO item = new ShoppingItemDTO();
            item.setTitle(detail.getVariant().getProduct().getTitle());
            item.setSlug(detail.getVariant().getProduct().getSlug());
            
            if (detail.getVariant().getImageUrl() != null) {
                item.setImage(detail.getVariant().getImageUrl());
            } else if (!detail.getVariant().getProduct().getImages().isEmpty()) {
                item.setImage(cloudinaryService.getImageUrl(detail.getVariant().getProduct().getImages().iterator().next().getImage()));
            }
            
            item.setPrice(detail.getVariant().getProduct().getPrice().toString());
            item.setQuantity(detail.getQuantity());
            
            String optionsStr = detail.getVariant().getOptions().stream()
                    .map(com.todoteg.model.VariantOption::getValue)
                    .collect(Collectors.joining(", "));
            item.setSize(optionsStr); // Using size as a generic label for variant details
            
            item.setPriceXquantity(detail.getPrice());
            return item;
        }).collect(Collectors.toList());
        
        UserProfile user = order.getAccount().getUser();
        OrderUserDTO userDTO = new OrderUserDTO();
        userDTO.setName(user.getName());
        userDTO.setPhoneNumber(user.getPhoneNumber());
        userDTO.setAddress(order.getAccount().getAddress() != null ? 
                order.getAccount().getAddress().toString() : "");
        
        OrderDetailResponse response = new OrderDetailResponse();
        response.setId(order.getId());
        response.setShopping(shoppingItems);
        response.setUser(userDTO);
        response.setAmount(order.getAmount());
        response.setDate(order.getDate());
        response.setStatus(order.getStatus());
        
        return response;
    }
    
    
    /**
     * Buscar todas las órdenes con filtros opcionales
     */
    @Transactional(readOnly = true)
    public Page<Order> findAllOrders(String statusStr, Long userId, Pageable pageable) {
        OrderStatus status = null;
        if (statusStr != null && !statusStr.isEmpty() && !statusStr.equals("ALL")) {
            status = OrderStatus.valueOf(statusStr.toUpperCase());
        }

        if (status != null && userId != null) {
            return orderRepository.findByStatusAndAccount_User_Id(status, userId, pageable);
        } else if (status != null) {
            return orderRepository.findByStatus(status, pageable);
        } else if (userId != null) {
            return orderRepository.findByAccount_User_Id(userId, pageable);
        }
        
        return orderRepository.findAll(pageable);
    }
    
    /**
     * Buscar orden por ID
     */
    @Transactional(readOnly = true)
    public Order findById(Long id) {
        return orderRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Orden no encontrada con ID: " + id));
    }
    
    /**
     * Buscar órdenes de un usuario específico
     */
    @Transactional(readOnly = true)
    public Page<Order> findOrdersByUserId(Long userId, Pageable pageable) {
        // Verificar que el usuario existe
        userRepository.findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con ID: " + userId));
        
        return orderRepository.findByAccount_User_Id(userId, pageable);
    }
    
    /**
     * Actualizar estado de una orden
     */
    @Transactional
    public Order updateOrderStatus(Long id, String statusStr) {
        Order order = findById(id);
        
        OrderStatus status;
        try {
            status = OrderStatus.valueOf(statusStr.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Estado inválido: " + statusStr);
        }
        
        order.setStatus(status);
        order = orderRepository.save(order);

        try {
            notificationService.notifyOrderStatusChanged(order);
        } catch (Exception e) {
            // best-effort
        }

        return order;
    }
    
    // Removing unused isValidStatus method as we rely on enum valueOf
    
    
}