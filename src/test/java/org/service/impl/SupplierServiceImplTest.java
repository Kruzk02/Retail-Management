package org.service.impl;

import org.dao.SupplierDao;
import org.dao.impl.SupplierDaoImpl;
import org.dto.SupplierRequest;
import org.exception.DataNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.model.Supplier;
import org.service.SupplierService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

class SupplierServiceImplTest {

    private SupplierDao dao;
    private SupplierService service;

    private Supplier supplier;
    @BeforeEach
    void setUp() {
        dao = Mockito.mock(SupplierDao.class);
        service = new SupplierServiceImpl(dao);

        supplier = Supplier.builder()
                .id(1L)
                .name("name")
                .contactName("contact")
                .address("address")
                .phoneNumber("+84 37 84 038")
                .email("test@gmail.com")
                .build();
    }

    @Test
    void testFindAll_success() {
        when(service.findAll(5,5)).thenReturn(List.of(supplier));

        var result = service.findAll(5,5);
        assertNotNull(result);
        assertEquals(List.of(supplier), result);
    }

    @Test
    void testFindAll_failed() {
        when(service.findAll(0,0)).thenReturn(null);

        var result = service.findAll(0,0);
        assertNull(result);
    }

    @Test
    void testFindById_success() {
        when(service.findById(1L)).thenReturn(supplier);

        var result = service.findById(1L);
        assertNotNull(result);
        assertEquals(supplier, result);
    }

    @Test
    void testFindById_failed() {
        when(service.findById(0L)).thenReturn(null);

        var result = service.findById(0L);
        assertNull(result);
    }

    @Test
    void testSave_success() {
        var request = new SupplierRequest(supplier.getName(), supplier.getContactName(), supplier.getPhoneNumber(), supplier.getEmail(), supplier.getAddress());
        when(service.save(request)).thenReturn(supplier);

        var result = service.save(request);
        assertNotNull(result);
        assertEquals(supplier, result);
    }

    @Test
    void testSave_failed(){

    }

    @Test
    void testUpdate_success() {
        var request = new SupplierRequest(supplier.getName(), supplier.getContactName(), supplier.getPhoneNumber(), supplier.getEmail(), supplier.getAddress());
        when(dao.isSupplierExists(1L)).thenReturn(true);
        when(dao.findById(1L)).thenReturn(supplier);
        when(service.update(1L, request)).thenReturn(supplier);

        var result = service.update(1L, request);

        assertNotNull(result);
        assertEquals(supplier, result);
    }

    @Test
    void testUpdate_failed() {
        var request = new SupplierRequest(supplier.getName(), supplier.getContactName(), supplier.getPhoneNumber(), supplier.getEmail(), supplier.getAddress());
        when(dao.isSupplierExists(0L)).thenReturn(false);

        var exception = assertThrows(DataNotFoundException.class, () -> service.update(0L, request));
        assertTrue(exception.getMessage().contains("not found"));
    }

    @Test
    void testDeleteById_success() {
        when(dao.isSupplierExists(1L)).thenReturn(true);
        when(service.deleteById(1L)).thenReturn(1);

        var result = service.deleteById(1L);

        assertEquals(1, result);
    }

    @Test
    void testDeleteById_failed() {
        when(dao.isSupplierExists(0L)).thenReturn(false);

        var exception = assertThrows(DataNotFoundException.class, () -> service.deleteById(0L));
        assertTrue(exception.getMessage().contains("not found"));
    }
}