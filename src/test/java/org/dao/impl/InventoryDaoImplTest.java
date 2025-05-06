package org.dao.impl;

import org.dao.InventoryDao;
import org.exception.DataNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.model.Category;
import org.model.Inventory;
import org.model.Location;
import org.model.Product;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

class InventoryDaoImplTest {

    private JdbcTemplate jdbcTemplate;
    private InventoryDao inventoryDao;

    private Inventory inventory;

    @BeforeEach
    void setUp() {
        jdbcTemplate = Mockito.mock(JdbcTemplate.class);
        inventoryDao = new InventoryDaoImpl(jdbcTemplate);

        inventory = Inventory.builder()
                .id(1L)
                .product(Product.builder()
                        .id(1L)
                        .name("Test Product")
                        .description("Test Description")
                        .price(BigDecimal.valueOf(100))
                        .categories(List.of(Category.builder().id(1L).build()))
                        .build())
                .location(Location.builder()
                        .id(1L)
                        .name("name")
                        .build())
                .quantity(123)
                .updatedAt(LocalDateTime.now())
                .build();
    }

    @Test
    void testIsExists_success() {

        when(jdbcTemplate.queryForObject(anyString(), eq(Boolean.class), eq(1L))).thenReturn(true);

        Boolean result = inventoryDao.isInventoryExist(1L);
        assertTrue(result);
    }

    @Test
    void testIExists_failed() {
        when(jdbcTemplate.queryForObject(anyString(), eq(Boolean.class), eq(0L))).thenReturn(false);

        Boolean result = inventoryDao.isInventoryExist(0L);
        assertFalse(result);
    }

    @Test
    void testSave_success() {
        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
        Map<String, Object> keyMap = new HashMap<>();
        keyMap.put("id", 1L);
        keyHolder.getKeyList().add(keyMap);

        when(jdbcTemplate.update(any(PreparedStatementCreator.class), any(KeyHolder.class)))
                .thenAnswer(invocation -> {
                    KeyHolder kh = invocation.getArgument(1);
                    kh.getKeyList().add(Map.of("id", 1L));
                    return 1;
                });

        when(jdbcTemplate.update(anyString(), anyLong(), anyLong())).thenReturn(1);

        Inventory result = inventoryDao.save(inventory);

        assertNotNull(result);
        assertEquals(inventory, result);
    }

    @Test
    void testSave_failed() {
        when(jdbcTemplate.update(any(PreparedStatementCreator.class), any(KeyHolder.class))).thenReturn(0);
        assertThrows(IllegalStateException.class, () -> inventoryDao.save(inventory));
    }

    @Test
    void testFindById_success() {
        long id = 1L;

        when(jdbcTemplate.queryForObject(anyString(), any(RowMapper.class), eq(id))).thenReturn(inventory);

        Inventory foundInventory = inventoryDao.findById(id);

        assertNotNull(foundInventory);
        assertEquals(inventory, foundInventory);
    }

    @Test
    void testFindById_failed() {
        Long id = 1L;

        when(jdbcTemplate.queryForObject(anyString(), any(RowMapper.class), eq(id))).thenThrow(new EmptyResultDataAccessException(1));

        assertThrows(DataNotFoundException.class, () -> inventoryDao.findById(id));
    }

    @Test
    void testUpdate_success() {
        Long id = 1L;

        when(jdbcTemplate.update(anyString(), any(Object[].class)))
                .thenReturn(1);

        Inventory updateInventory = inventoryDao.update(id, inventory);

        assertNotNull(updateInventory);
        assertEquals(inventory, updateInventory);
    }

    @Test
    void testUpdate_failed() {
        Long id = 1L;

        Inventory inventory1 = Inventory.builder().build();
        assertThrows(NullPointerException.class, () -> inventoryDao.update(id, inventory1));
    }

    @Test
    void testDeleteById_success() {

        when(jdbcTemplate.update(anyString(), eq(1L)))
                .thenReturn(1);

        int rows = inventoryDao.deleteById(1L);

        assertEquals(1, rows);
    }

    @Test
    void testDeleteById_failed() {
        when(jdbcTemplate.update(anyString(), eq(0L)))
                .thenReturn(0);

        assertThrows(DataNotFoundException.class, () -> inventoryDao.deleteById(0L));
    }
}