package com.todoteg.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "store_variant_type")
@Getter
@Setter
@ToString(exclude = "options")
@NoArgsConstructor
@AllArgsConstructor
public class VariantType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name; // e.g. "Color", "Talla", "Modelo"
    
    // Optional: a display type to tell the frontend how to render it (e.g. "color", "button", "select")
    @Column(length = 20)
    private String type = "button"; 

    @OneToMany(mappedBy = "variantType", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<VariantOption> options = new HashSet<>();
}
