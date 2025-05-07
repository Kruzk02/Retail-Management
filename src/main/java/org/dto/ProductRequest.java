package org.dto;

import java.math.BigDecimal;
import java.util.Collection;

public record ProductRequest(
    String name,
    String description,
    BigDecimal price,
    Collection<String> categories
) {
    public ProductRequest {

        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Product name must not be null or blank.");
        }

        if (description == null || description.isBlank()) {
            throw new IllegalArgumentException("Description must not be null or blank.");
        }

        if (price == null || price.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Price must not be null or negative.");
        }

        if (categories == null || categories.isEmpty()) {
            throw new IllegalArgumentException("Categories must not be null or empty.");
        }

        for (String category : categories) {
            if (category == null || category.isBlank()) {
                throw new IllegalArgumentException("Category list must not contain null or blank values.");
            }
        }
    }
}
