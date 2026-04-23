package com.todoteg.model;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import java.util.stream.Stream;

@Converter(autoApply = true)
public class OrderStatusConverter implements AttributeConverter<OrderStatus, String> {

    @Override
    public String convertToDatabaseColumn(OrderStatus status) {
        if (status == null) {
            return null;
        }
        return status.getDescription();
    }

    @Override
    public OrderStatus convertToEntityAttribute(String dbData) {
        if (dbData == null) {
            return null;
        }

        return Stream.of(OrderStatus.values())
          .filter(s -> s.getDescription().equalsIgnoreCase(dbData) || s.name().equalsIgnoreCase(dbData))
          .findFirst()
          .orElse(OrderStatus.PENDING); // Fallback to avoid crashes with legacy data
    }
}
