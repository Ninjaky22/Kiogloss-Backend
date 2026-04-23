package com.todoteg.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "store_product_variant")
@Getter
@Setter
@ToString(exclude = {"product", "options"})
@NoArgsConstructor
@AllArgsConstructor
public class ProductVariant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name = "store_product_variant_options",
        joinColumns = @JoinColumn(name = "variant_id"),
        inverseJoinColumns = @JoinColumn(name = "option_id")
    )
    private java.util.Set<VariantOption> options = new java.util.HashSet<>();

    @Column(nullable = false)
    private Integer stock = 0;

    @Column(unique = true)
    private String sku;

    private String imageUrl;
}
