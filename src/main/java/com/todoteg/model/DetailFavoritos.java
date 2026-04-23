package com.todoteg.model;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonBackReference;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "store_detail_favoritos")
@Getter
@Setter
@ToString(exclude = "account")
@NoArgsConstructor
@AllArgsConstructor
public class DetailFavoritos {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "account_id", nullable = false)
    @JsonBackReference
    private Account account;
    
    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    
    private Product product;
    
    @Column(nullable = false)
    private LocalDate date;
    
    @Column(nullable = false)
    private Boolean status = true;
    
    @PrePersist
    protected void onCreate() {
        if (date == null) {
            date = LocalDate.now();
        }
    }
}