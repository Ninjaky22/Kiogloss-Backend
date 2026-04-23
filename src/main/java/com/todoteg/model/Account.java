package com.todoteg.model;

import java.util.HashSet;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonBackReference;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "store_account")
@Getter
@Setter
@ToString(exclude = {"user", "favorites", "orders"})
@NoArgsConstructor
@AllArgsConstructor
public class Account {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @OneToOne
    @JoinColumn(name = "user_id", unique = true, nullable = false)
    @JsonBackReference
    private UserProfile user;
    
    @ManyToOne
    @JoinColumn(name = "address_id")
    private Address address;
    
    @Column(name = "points_per_purchase")
    private Integer pointsPerPurchase = 0;
    
    @Column(name = "is_active")
    private Boolean isActive = true;
    
    @OneToMany(mappedBy = "account", cascade = CascadeType.ALL)
    @JsonBackReference
    private Set<DetailFavoritos> favorites = new HashSet<>();
    
    @OneToMany(mappedBy = "account", cascade = CascadeType.ALL)
    @JsonBackReference
    private Set<Order> orders = new HashSet<>();
}