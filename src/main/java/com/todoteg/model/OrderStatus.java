package com.todoteg.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum OrderStatus {
    PENDING("Pendiente"),
    PROCESSING("En Preparacion"),
    SHIPPED("Enviado"),
    DELIVERED("Entregado"),
    CANCELLED("Cancelado");

    private final String description;

    OrderStatus(String description) {
        this.description = description;
    }

    @JsonValue
    public String getDescription() {
        return description;
    }

    @JsonCreator
    public static OrderStatus fromString(String value) {
        if (value == null) return null;
        for (OrderStatus status : OrderStatus.values()) {
            if (status.name().equalsIgnoreCase(value) || 
                status.description.equalsIgnoreCase(value)) {
                return status;
            }
        }
        // Handle variations
        if (value.equalsIgnoreCase("processing")) return PROCESSING;
        if (value.equalsIgnoreCase("en preparacion")) return PROCESSING;
        if (value.equalsIgnoreCase("pending")) return PENDING;
        
        throw new IllegalArgumentException("Unknown status: " + value);
    }
}
