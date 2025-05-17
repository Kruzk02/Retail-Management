package org.dao.impl;

import org.dao.SupplierDao;
import org.exception.DataNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.model.Supplier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

class SupplierDaoImplTest {
    
    private JdbcTemplate jdbcTemplate;
    private SupplierDao supplierDao;

    private Supplier supplier;

    @BeforeEach
    void setUp() {
        jdbcTemplate = Mockito.mock(JdbcTemplate.class);
        supplierDao = new SupplierDaoImpl(jdbcTemplate);

        supplier = Supplier.builder()
                .id(1L)
                .name("name")
                .contactName("contact")
                .address("address")
                .phoneNumber("phone")
                .email("email")
                .build();
    }

    @Test
    void testIsSupplierExists_success() {
        when(jdbcTemplate.queryForObject(anyString(), eq(Boolean.class), eq(1L))).thenReturn(true);

        Boolean result = supplierDao.isSupplierExists(1L);
        assertTrue(result);
    }

    @Test
    void testIsSupplierExists_failed() {
        when(jdbcTemplate.queryForObject(anyString(), eq(Boolean.class), eq(0L))).thenReturn(false);

        Boolean result = supplierDao.isSupplierExists(0L);
        assertFalse(result);
    }

    @Test
    void testSave_success() {
        var keyHolder = new GeneratedKeyHolder();
        Map<String, Object> keyMap = new HashMap<>();
        keyMap.put("id", 1);
        keyHolder.getKeyList().add(keyMap);

        when(jdbcTemplate.update(any(PreparedStatementCreator.class), any(KeyHolder.class)))
                .thenAnswer(invocation -> {
                    KeyHolder kh = invocation.getArgument(1);
                    kh.getKeyList().add(Map.of("id", 1));
                    return 1;
                });

        when(jdbcTemplate.update(anyString(), anyLong(), anyLong())).thenReturn(1);

        Supplier savedSupplier = supplierDao.save(supplier);

        assertNotNull(savedSupplier);
    }

    @Test
    void testSave_failed() {
        when(jdbcTemplate.update(any(PreparedStatementCreator.class), any(KeyHolder.class))).thenReturn(0);

        assertThrows(IllegalStateException.class, () -> supplierDao.save(supplier));
    }

    @Test
    void testFindById_success() {
        when(jdbcTemplate.queryForObject(anyString(), any(RowMapper.class), eq(1L))).thenReturn(supplier);

        Supplier result = supplierDao.findById(1L);

        assertNotNull(result);
        assertEquals(supplier,result);
    }

    @Test
    void testFindBy_failed() {
        when(jdbcTemplate.queryForObject(anyString(), any(RowMapper.class), eq(0L))).thenReturn(null);

        Supplier result = supplierDao.findById(0L);

        assertNull(result);
    }

    @Test
    void testFindAll_success() {
        when(jdbcTemplate.query(anyString(), any(RowMapper.class), eq(5), eq(5))).thenReturn(List.of(supplier));

        List<Supplier> result = supplierDao.findAll(5,5);

        assertNotNull(result);
    }

    @Test
    void testUpdate_success() {
        when(jdbcTemplate.update(anyString(), any(Object[].class))).thenReturn(1);

        Supplier result = supplierDao.update(1L, supplier);

        assertNotNull(result);
    }

    @Test
    void testUpdate_failed() {
        var supplier = Supplier.builder().build();

        assertThrows(IllegalArgumentException.class, () -> supplierDao.update(1L, supplier));
    }

    @Test
    void testDeleteById_success() {
        when(jdbcTemplate.update(anyString(), eq(1L))).thenReturn(1);

        int result = supplierDao.deleteById(1L);
        assertEquals(1, result);
    }

    @Test
    void testDeleteById_failed() {
        when(jdbcTemplate.update(anyString(), eq(0L))).thenReturn(0);

        assertThrows(DataNotFoundException.class, () -> supplierDao.deleteById(0L));
    }
}