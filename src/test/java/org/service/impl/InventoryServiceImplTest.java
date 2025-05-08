package org.service.impl;

import org.dao.InventoryDao;
import org.dao.LocationDao;
import org.dao.ProductDao;
import org.dto.InventoryRequest;
import org.exception.DataNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.model.Inventory;
import org.model.Location;
import org.model.Product;
import org.service.InventoryService;
import org.springframework.dao.EmptyResultDataAccessException;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class InventoryServiceImplTest {

    private ProductDao productDao;
    private LocationDao locationDao;
    private InventoryDao inventoryDao;

    private InventoryService inventoryService;

    private Inventory inventory;
    private Product product;
    private Location location;

    @BeforeEach
    void setUp() {
        productDao = Mockito.mock(ProductDao.class);
        locationDao = Mockito.mock(LocationDao.class);
        inventoryDao = Mockito.mock(InventoryDao.class);

        inventoryService = new InventoryServiceImpl(inventoryDao, productDao, locationDao);

        product = Product.builder()
                .id(1L)
                .name("name")
                .description("description")
                .price(BigDecimal.ONE)
                .categories(List.of())
                .created_at(LocalDateTime.now())
                .build();

        location = Location.builder()
                .id(1L)
                .name("name")
                .build();

        inventory = Inventory.builder()
            .id(1L)
            .product(product)
            .location(location)
            .quantity(20)
            .build();
    }

    @Test
    void testSave_success() {
        InventoryRequest request = new InventoryRequest(inventory.getProduct().getId(), inventory.getLocation().getId(), inventory.getQuantity());
        when(productDao.isProductExists(1L)).thenReturn(true);
        when(locationDao.isLocationExist(1L, null)).thenReturn(true);
        when(productDao.findById(1L)).thenReturn(product);
        when(locationDao.findById(1L)).thenReturn(location);
        when(inventoryService.save(request)).thenReturn(inventory);

        Inventory result = inventoryService.save(request);
        assertNotNull(result);
        assertEquals(inventory, result);
    }

    @Test
    void testSave_failed() {
        when(productDao.isProductExists(1L)).thenReturn(false);
        when(locationDao.isLocationExist(1L, null)).thenReturn(false);

        Exception exception = assertThrows(DataNotFoundException.class, () -> inventoryService.save(new InventoryRequest(inventory.getProduct().getId(), inventory.getLocation().getId(), inventory.getQuantity())));
        assertTrue(exception.getMessage().contains("not found"));
    }

    @Test
    void testFindById_success() {
        when(inventoryService.findById(1L)).thenReturn(inventory);

        Inventory result = inventoryService.findById(1L);

        assertNotNull(result);
        assertEquals(inventory, result);
        verify(inventoryDao).findById(1L);
    }

    @Test
    void testFindById_notFound() {
        when(inventoryService.findById(0L)).thenThrow(new EmptyResultDataAccessException(1));

        Inventory result = inventoryService.findById(1L);
        assertNull(result);
    }

    @Test
    void testUpdate_success() {
        InventoryRequest request = new InventoryRequest(inventory.getProduct().getId(), inventory.getLocation().getId(), inventory.getQuantity());
        when(productDao.isProductExists(1L)).thenReturn(true);
        when(locationDao.isLocationExist(1L, null)).thenReturn(true);
        when(inventoryDao.isInventoryExist(1L)).thenReturn(true);

        when(productDao.findById(1L)).thenReturn(product);
        when(locationDao.findById(1L)).thenReturn(location);
        when(inventoryDao.findById(1L)).thenReturn(inventory);
        when(inventoryService.update(1L, request)).thenReturn(inventory);

        Inventory result = inventoryService.update(1L, request);

        assertNotNull(result);
        assertEquals(inventory, result);
    }

    @Test
    void testUpdate_notFound() {
        InventoryRequest request = new InventoryRequest(inventory.getProduct().getId(), inventory.getLocation().getId(), inventory.getQuantity());
        when(inventoryDao.isInventoryExist(0L)).thenReturn(false);

        Exception exception = assertThrows(DataNotFoundException.class, () -> inventoryService.update(0L,  request));

        assertTrue(exception.getMessage().contains("not found"));
    }

    @Test
    void testDeleteById_success() {
        when(inventoryDao.isInventoryExist(1L)).thenReturn(true);
        when(inventoryService.deleteById(1L)).thenReturn(1);

        int result = inventoryService.deleteById(1L);

        assertEquals(1 ,result);
    }

    @Test
    void testDeleteById_failed() {
        when(inventoryDao.isInventoryExist(1L)).thenReturn(false);

        Exception exception = assertThrows(DataNotFoundException.class, () -> inventoryService.deleteById(1L));

        assertTrue(exception.getMessage().contains("not found"));
    }
}