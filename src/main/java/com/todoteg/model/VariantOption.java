package com.todoteg.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "store_variant_option")
@Getter
@Setter
@ToString(exclude = "variantType")
@NoArgsConstructor
@AllArgsConstructor
public class VariantOption {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "variant_type_id", nullable = false)
    @JsonIgnore
    private VariantType variantType;

    @Column(nullable = false)
    private String value; // e.g. "Rojo", "M", "GTX 1060"

    // Only used if VariantType.type == "color"
    private String metaValue; // e.g. "#FF0000"
}
