package org.dto;

public record InventoryRequest(Long productId, Long locationId, Integer quantity) {
    public InventoryRequest {
        if (productId <= 0) {
            throw new IllegalArgumentException("Product id cannot less than or equal 0");
        }

        if (locationId <= 0) {
            throw new IllegalArgumentException("Location id cannot less than or equal 0");
        }
    }
}
