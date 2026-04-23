package com.todoteg.mapper;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import com.todoteg.dto.AddressDTO;
import com.todoteg.dto.OrderDetailResponse;
import com.todoteg.dto.OrderUserDTO;
import com.todoteg.dto.ShoppingItemDTO;
import com.todoteg.dto.UserDetailResponse;
import com.todoteg.dto.admin.CustomerBasicDTO;
import com.todoteg.dto.admin.OrderDetailDTO;
import com.todoteg.dto.admin.OrderItemDTO;
import com.todoteg.dto.admin.OrderSummaryDTO;
import com.todoteg.dto.admin.PageResponseDTO;
import com.todoteg.dto.admin.UserDashboardDTO;
import com.todoteg.dto.admin.ProductBasicDTO;
import com.todoteg.dto.admin.UserDetailDTO;
import com.todoteg.model.Address;
import com.todoteg.model.DetailOrder;
import com.todoteg.model.Order;
import com.todoteg.model.UserProfile;

@Component
public class UserMapper {

    public UserDashboardDTO toUserDashboardDTO(UserProfile user) {
        UserDashboardDTO dto = new UserDashboardDTO();
        dto.setId(user.getId());
        dto.setEmail(user.getEmail());
        dto.setName(user.getName());
        dto.setPhoneNumber(user.getPhoneNumber());
        dto.setProfileImage(user.getProfileImage());
        dto.setIsActive(user.getIsActive());
        dto.setIsStaff(user.getIsStaff());
        dto.setIsSuperuser(user.getIsSuperuser());
        
        if (user.getAccount() != null) {
            dto.setTotalOrders(user.getAccount().getOrders().size());
            dto.setTotalFavorites(user.getAccount().getFavorites().size());
        }
        
        return dto;
    }

    public UserDetailDTO toUserDetailDTO(UserProfile user) {
        UserDetailDTO dto = new UserDetailDTO();
        dto.setId(user.getId());
        dto.setEmail(user.getEmail());
        dto.setName(user.getName());
        dto.setPhoneNumber(user.getPhoneNumber());
        dto.setProfileImage(user.getProfileImage());
        dto.setIsActive(user.getIsActive());
        dto.setIsStaff(user.getIsStaff());
        dto.setIsSuperuser(user.getIsSuperuser());
        
        return dto;
    }

    public UserDetailResponse toUserDetailResponse(UserProfile user) {
        UserDetailResponse response = new UserDetailResponse();
        response.setId(user.getId());
        response.setEmail(user.getEmail());
        response.setName(user.getName());
        response.setPhoneNumber(user.getPhoneNumber());
        response.setProfileImage(user.getProfileImage());
        // response.setIsActive(user.getIsActive()); // Removed from DTO? 
        
        return response;
    }

    public CustomerBasicDTO toCustomerBasicDTO(UserProfile user) {
        CustomerBasicDTO dto = new CustomerBasicDTO();
        dto.setUserId(user.getId());
        dto.setName(user.getName());
        dto.setEmail(user.getEmail());
        dto.setProfileImage(user.getProfileImage());
        return dto;
    }

    public OrderSummaryDTO toOrderSummaryDTO(Order order) {
        OrderSummaryDTO dto = new OrderSummaryDTO();
        dto.setId(order.getId());
        dto.setAmount(order.getAmount());
        dto.setDate(order.getDate());
        dto.setStatus(order.getStatus() != null ? order.getStatus().getDescription() : "Pendiente");
        dto.setItemCount(order.getShopping().size());
        
        if (order.getAccount() != null && order.getAccount().getUser() != null) {
            dto.setCustomer(toCustomerBasicDTO(order.getAccount().getUser()));
        }
        
        return dto;
    }
    
    public OrderDetailDTO toOrderDetailDTO(Order order) {
        OrderDetailDTO dto = new OrderDetailDTO();
        dto.setId(order.getId());
        dto.setAmount(order.getAmount());
        dto.setDate(order.getDate());
        dto.setStatus(order.getStatus() != null ? order.getStatus().getDescription() : "Pendiente");
        
        if (order.getAccount() != null && order.getAccount().getUser() != null) {
            dto.setCustomer(toCustomerBasicDTO(order.getAccount().getUser()));
        }
        
        List<OrderItemDTO> itemDTOs = order.getShopping().stream().map(detail -> {
            OrderItemDTO item = new OrderItemDTO();
            item.setId(detail.getId());
            item.setQuantity(detail.getQuantity());
            item.setPrice(detail.getPrice());
            
            String optionsStr = detail.getVariant().getOptions().stream()
                    .map(com.todoteg.model.VariantOption::getValue)
                    .collect(Collectors.joining(", "));
            item.setSize(optionsStr);
            item.setColor("");
            
            ProductBasicDTO productBrief = new ProductBasicDTO();
            productBrief.setId(detail.getVariant().getProduct().getId());
            productBrief.setTitle(detail.getVariant().getProduct().getTitle());
            productBrief.setSlug(detail.getVariant().getProduct().getSlug());
            
            if (detail.getVariant().getImageUrl() != null) {
                productBrief.setFirstImage(detail.getVariant().getImageUrl());
            } else if (!detail.getVariant().getProduct().getImages().isEmpty()) {
                productBrief.setFirstImage(detail.getVariant().getProduct().getImages().iterator().next().getImage());
            }
            
            item.setProduct(productBrief);
            return item;
        }).collect(Collectors.toList());
        
        dto.setItems(itemDTOs);
        return dto;
    }

    public PageResponseDTO<OrderSummaryDTO> toOrderSummaryPage(Page<Order> page) {
        List<OrderSummaryDTO> content = page.getContent().stream()
                .map(this::toOrderSummaryDTO)
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

    public PageResponseDTO<UserDashboardDTO> toUserDashboardPage(Page<UserProfile> page) {
        List<UserDashboardDTO> content = page.getContent().stream()
                .map(this::toUserDashboardDTO)
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

    public AddressDTO toAddressDTO(Address address) {
        if (address == null) return null;
        AddressDTO dto = new AddressDTO();
        dto.setId(address.getId());
        dto.setStreet(address.getStreet());
        dto.setStreetNumber(address.getStreetNumber());
        dto.setDistric(address.getDistric());
        return dto;
    }
}
