package org.service.impl;

import org.dao.SupplierDao;
import org.dao.impl.SupplierDaoImpl;
import org.dto.SupplierRequest;
import org.exception.DataNotFoundException;
import org.exception.InvalidValidatorException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.model.Supplier;
import org.service.SupplierService;
import org.springframework.validation.Errors;
import org.validators.SupplierRequestValidator;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.when;

class SupplierServiceImplTest {

    private SupplierDao dao;
    private SupplierRequestValidator supplierRequestValidator;
    private SupplierService service;

    private Supplier supplier;
    private SupplierRequest supplierRequest;

    @BeforeEach
    void setUp() {
        dao = Mockito.mock(SupplierDao.class);
        supplierRequestValidator = Mockito.mock(SupplierRequestValidator.class);
        service = new SupplierServiceImpl(dao, supplierRequestValidator);

        supplier = Supplier.builder()
                .id(1L)
                .name("name")
                .contactName("contact")
                .address("address")
                .phoneNumber("+84 37 84 038")
                .email("test@gmail.com")
                .build();

        supplierRequest = new SupplierRequest(supplier.getName(), supplier.getContactName(), supplier.getPhoneNumber(), supplier.getEmail(), supplier.getAddress());
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
        when(service.save(supplierRequest)).thenReturn(supplier);

        var result = service.save(supplierRequest);
        assertNotNull(result);
        assertEquals(supplier, result);
    }

    @Test
    void testSave_failed(){
        doAnswer(invocation -> {
            Errors errors = invocation.getArgument(1);
            errors.rejectValue("name", "invalid", "Name must not be empty");
            errors.rejectValue("contactName", "invalid", "Contact must not be empty");
            errors.rejectValue("phoneNumber", "invalid", "Phone number must not be empty");
            errors.rejectValue("email", "invalid", "Email must not be empty");
            errors.rejectValue("address", "invalid", "Address must not be empty");
            return null;
        }).when(supplierRequestValidator).validate(eq(supplierRequest), any(Errors.class));

        InvalidValidatorException exception = assertThrows(InvalidValidatorException.class, () -> service.save(supplierRequest));

        List<String> message = exception.getAllMessage();
        assertEquals("Name must not be empty", message.getFirst());
        assertEquals("Contact must not be empty", message.get(1));
        assertEquals("Phone number must not be empty", message.get(2));
        assertEquals("Email must not be empty", message.get(3));
        assertEquals("Address must not be empty", message.getLast());
    }

    @Test
    void testUpdate_success() {
        when(dao.isSupplierExists(1L)).thenReturn(true);
        when(dao.findById(1L)).thenReturn(supplier);
        when(service.update(1L, supplierRequest)).thenReturn(supplier);

        var result = service.update(1L, supplierRequest);

        assertNotNull(result);
        assertEquals(supplier, result);
    }

    @Test
    void testUpdate_failed() {
        when(dao.isSupplierExists(0L)).thenReturn(false);

        var exception = assertThrows(DataNotFoundException.class, () -> service.update(0L, supplierRequest));
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