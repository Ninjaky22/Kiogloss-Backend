package com.todoteg.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "store_cart_item")
@Data
public class CartItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "account_id", nullable = false)
    private Account account;
    
    @ManyToOne
    @JoinColumn(name = "variant_id", nullable = false)
    private ProductVariant variant;
    
    @Column(nullable = false)
    private Integer quantity = 1;
}
