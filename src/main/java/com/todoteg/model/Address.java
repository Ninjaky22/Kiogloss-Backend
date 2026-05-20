package com.todoteg.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "store_address")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Address {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String street;
    
    @Column(name = "street_number", nullable = false)
    private String streetNumber;
    
    @Column(nullable = false)
    private String distric;

    @Column
    private String city;

    @Override
    public String toString() {
        return street + " " + streetNumber + " " + distric + (city != null ? ", " + city : "");
    }
}