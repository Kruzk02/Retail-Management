package org.dao;

import org.model.Inventory;

public interface InventoryDao {
    Boolean isInventoryExist(Long id);
    Inventory save(Inventory inventory);
    Inventory findById(Long id);
    Inventory update(Long id, Inventory inventory);
    int deleteById(Long id);
}
