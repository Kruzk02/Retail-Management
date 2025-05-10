package org.service.impl;

import lombok.AllArgsConstructor;
import org.dao.InventoryDao;
import org.dao.LocationDao;
import org.dao.ProductDao;
import org.dto.InventoryRequest;
import org.exception.DataNotFoundException;
import org.model.Inventory;
import org.model.Location;
import org.model.Product;
import org.service.InventoryService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@AllArgsConstructor
@Service
public class InventoryServiceImpl implements InventoryService {

    private final InventoryDao inventoryDao;
    private final ProductDao productDao;
    private final LocationDao locationDao;

    @Override
    public Inventory save(InventoryRequest request) {
        Boolean isProductExists = productDao.isProductExists(request.productId());
        if (!isProductExists) {
            throw new DataNotFoundException("Product not found with a id: " + request.productId());
        }

        Boolean isLocationExists = locationDao.isLocationExist(request.locationId(), null);
        if (!isLocationExists) {
            throw new DataNotFoundException("Location not found with a id: " + request.locationId());
        }

        return inventoryDao.save(Inventory.builder()
                .product(productDao.findById(request.locationId()))
                .location(locationDao.findById(request.locationId()))
                .quantity(request.quantity())
                .build());
    }

    @Override
    public Inventory findById(Long id) {
        return inventoryDao.findById(id);
    }

    @Override
    public Inventory update(Long id, InventoryRequest request) {
        Boolean isProductExists = productDao.isProductExists(request.productId());
        if (!isProductExists) {
            throw new DataNotFoundException("Product not found with a id: " + request.productId());
        }

        Product product = productDao.findById(request.locationId());

        Boolean isLocationExists = locationDao.isLocationExist(request.locationId(), null);
        if (!isLocationExists) {
            throw new DataNotFoundException("Location not found with a id: " + request.locationId());
        }

        Location location = locationDao.findById(request.locationId());

        Boolean isInventoryExists = inventoryDao.isInventoryExist(id);
        if (!isInventoryExists) {
            throw new DataNotFoundException("Inventory not found with a id: " + id);
        }

        Inventory inventory = inventoryDao.findById(id);
        inventory.setProduct(product);
        inventory.setLocation(location);
        inventory.setQuantity(request.quantity());
        inventory.setUpdatedAt(LocalDateTime.now());

        return inventoryDao.update(id, inventory);
    }

    @Override
    public int deleteById(Long id) {
        Boolean isExists = inventoryDao.isInventoryExist(id);
        if (!isExists) {
            throw new DataNotFoundException("Inventory not found with a id: " + id);
        }
        return inventoryDao.deleteById(id);
    }
}
