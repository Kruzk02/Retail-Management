package org.service;

import org.dto.InventoryRequest;
import org.model.Inventory;

public interface InventoryService {
    Inventory save(InventoryRequest request);
    Inventory findById(Long id);
    Inventory update(Long id, InventoryRequest request);
    int deleteById(Long id);
}
