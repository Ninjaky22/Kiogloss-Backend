package com.todoteg.model;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "store_order")
@Getter
@Setter
@ToString(exclude = {"account", "shopping"})
@NoArgsConstructor
@AllArgsConstructor
public class Order {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "account_id", nullable = false)
    @JsonBackReference
    private Account account;
    
    @Column(nullable = false)
    private Double amount = 0.0;
    
    @Column(nullable = false)
    private LocalDate date;
    
    @Column(length = 20, nullable = false)
    private OrderStatus status = OrderStatus.PENDING;
    
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
    @JsonIgnore
    private Set<DetailOrder> shopping = new HashSet<>();
    
    @PrePersist
    protected void onCreate() {
        if (date == null) {
            date = LocalDate.now();
        }
    }
}