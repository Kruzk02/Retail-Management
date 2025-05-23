package org.dto;

public record InventoryRequest(Long productId, Long locationId, Integer quantity) { }
