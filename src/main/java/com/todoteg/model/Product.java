package com.todoteg.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import java.util.Map;
import java.util.HashMap;

@Entity
@Table(name = "store_product")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Product {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String title;
    
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;
    
    @Column(columnDefinition = "TEXT")
    private String description;
    
    @Column(unique = true, nullable = false)
    private String slug;
    
    @Column(length = 10)
    private String status = "draft";
    
    @Column(nullable = false)
    private LocalDateTime published;
    
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "store_product_attributes", joinColumns = @JoinColumn(name = "product_id"))
    @MapKeyColumn(name = "attribute_name")
    @Column(name = "attribute_value")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Map<String, String> attributes = new HashMap<>();
    
    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Set<ProductVariant> variants = new HashSet<>();
    
    @ManyToMany
    @JoinTable(
        name = "store_product_tag",
        joinColumns = @JoinColumn(name = "product_id"),
        inverseJoinColumns = @JoinColumn(name = "tag_id")
    )
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Set<Tag> tags = new HashSet<>();
    
    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Set<Images> images = new HashSet<>();
    
    @PrePersist
    protected void onCreate() {
        if (published == null) {
            published = LocalDateTime.now();
        }
    }
}