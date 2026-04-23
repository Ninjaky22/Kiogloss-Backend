package com.todoteg.controller;

import com.todoteg.model.*;
import com.todoteg.repository.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/user/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartItemRepository cartItemRepository;
    private final AccountRepository accountRepository;
    private final com.todoteg.repository.ProductVariantRepository variantRepository;

    @GetMapping
    public ResponseEntity<Map<String, Object>> getCart(
            Authentication auth,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "8") int page_size) {

        UserProfile user = (UserProfile) auth.getPrincipal();
        Account account = accountRepository.findByUserId(user.getId()).orElseThrow();

        Page<CartItem> cartPage = cartItemRepository.findByAccountId(
                account.getId(), PageRequest.of(page, page_size));

        BigDecimal grandTotal = cartItemRepository.sumGrandTotalByAccountId(account.getId());

        List<Map<String, Object>> items = cartPage.getContent().stream().map(item -> {
            Map<String, Object> dto = new HashMap<>();
            dto.put("id", item.getId());
            dto.put("productId", item.getVariant().getProduct().getId());
            dto.put("variantId", item.getVariant().getId());
            dto.put("name", item.getVariant().getProduct().getTitle());
            dto.put("price", item.getVariant().getProduct().getPrice());
            dto.put("slug", item.getVariant().getProduct().getSlug());
            dto.put("quantity", item.getQuantity());

            String optionsStr = item.getVariant().getOptions().stream()
                    .map(com.todoteg.model.VariantOption::getValue)
                    .collect(Collectors.joining(", "));
            dto.put("variantDetails", optionsStr);
            dto.put("stock", item.getVariant().getStock());

            if (item.getVariant().getImageUrl() != null) {
                dto.put("image", item.getVariant().getImageUrl());
            } else if (!item.getVariant().getProduct().getImages().isEmpty()) {
                dto.put("image", item.getVariant().getProduct().getImages().iterator().next().getImage());
            }
            return dto;
        }).collect(Collectors.toList());

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("grandTotal", grandTotal);
        response.put("content", items);
        response.put("totalElements", cartPage.getTotalElements());
        response.put("totalPages", cartPage.getTotalPages());
        response.put("number", cartPage.getNumber());

        return ResponseEntity.ok(response);
    }
    @PostMapping
    @Transactional
    public ResponseEntity<?> addToCart(Authentication auth, @RequestBody Map<String, Object> req) {
        UserProfile user = (UserProfile) auth.getPrincipal();
        Account account = accountRepository.findByUserId(user.getId()).orElseThrow();
        Long variantId = Long.valueOf(req.get("variantId").toString());
        com.todoteg.model.ProductVariant variant = variantRepository.findById(variantId).orElseThrow();

        Integer qty = Integer.valueOf(req.get("quantity").toString());

        CartItem item = cartItemRepository.findByAccountIdAndVariantId(account.getId(), variantId)
                .orElse(new CartItem());

        if (item.getId() == null) {
            item.setAccount(account);
            item.setVariant(variant);
            item.setQuantity(qty);
        } else {
            item.setQuantity(item.getQuantity() + qty);
        }
        cartItemRepository.save(item);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{id}")
    @Transactional
    public ResponseEntity<?> updateQuantity(@PathVariable Long id, @RequestParam Integer quantity) {
        CartItem item = cartItemRepository.findById(id).orElseThrow();
        item.setQuantity(quantity);
        cartItemRepository.save(item);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    @Transactional
    public ResponseEntity<?> removeItem(@PathVariable Long id) {
        cartItemRepository.deleteById(id);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping
    @Transactional
    public ResponseEntity<?> clearCart(Authentication auth) {
        UserProfile user = (UserProfile) auth.getPrincipal();
        Account account = accountRepository.findByUserId(user.getId()).orElseThrow();
        cartItemRepository.deleteByAccountId(account.getId());
        return ResponseEntity.ok().build();
    }
}
