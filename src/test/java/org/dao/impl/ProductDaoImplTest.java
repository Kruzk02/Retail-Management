package org.dao.impl;

import org.dao.ProductDao;
import org.exception.DataNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.model.Category;
import org.model.Product;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

class ProductDaoImplTest {
    private JdbcTemplate jdbcTemplate;
    private ProductDao productDao;

    private Product product;

    @BeforeEach
    void setUp() {
        jdbcTemplate = Mockito.mock(JdbcTemplate.class);
        productDao = new ProductDaoImpl(jdbcTemplate);

        product = Product.builder()
                .id(1L)
                .name("Test Product")
                .description("Test Description")
                .price(BigDecimal.valueOf(100))
                .categories(List.of(Category.builder().id(1L).build()))
                .build();
    }

    @Test
    void testIsExists_shouldSuccess() {

        when(jdbcTemplate.queryForObject(anyString(), eq(Boolean.class), eq(1L))).thenReturn(true);

        Boolean result = productDao.isProductExists(1L);
        assertTrue(result);
    }

    @Test
    void testIsExists_shouldNotFound() {

        when(jdbcTemplate.queryForObject(anyString(), eq(Boolean.class), eq(0L))).thenReturn(false);

        Boolean result = productDao.isProductExists(0L);
        assertFalse(result);
    }

    @Test
    void testSave_shouldInsertSuccessfully() {
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

        Product savedProduct = productDao.save(product);

        assertNotNull(savedProduct);
        assertEquals(1L, savedProduct.getId());
    }

    @Test
    void testSave_shouldThrowException_WhenInsertFails() {
        when(jdbcTemplate.update(any(PreparedStatementCreator.class), any(KeyHolder.class))).thenReturn(0);

        assertThrows(IllegalStateException.class, () -> productDao.save(product));
    }

    @Test
    void testFindById_shouldReturnProduct_whenFound() {
        long id = 1L;

        when(jdbcTemplate.query(anyString(), any(ResultSetExtractor.class), eq(id))).thenReturn(product);

        Product foundProduct = productDao.findById(id);

        assertNotNull(foundProduct);
        assertEquals(product, foundProduct);
    }

    @Test
    void testFindById_shouldThrowException_whenNotFound() {
        Long id = 1L;

        when(jdbcTemplate.query(anyString(), any(ResultSetExtractor.class), eq(id))).thenThrow(new DataNotFoundException(""));

        assertThrows(DataNotFoundException.class, () -> productDao.findById(id));
    }

    @Test
    void update_ShouldUpdateFields_WhenFieldsPresent() {
        Long id = 1L;

        when(jdbcTemplate.update(anyString(), any(Object[].class)))
                .thenReturn(1);

        Product updatedProduct = productDao.update(id, product);

        assertNotNull(updatedProduct);
        assertEquals(product.getName(), updatedProduct.getName());
    }

    @Test
    void update_ShouldThrowException_WhenNoFieldsProvided() {
        Long id = 1L;
        Product product = Product.builder().build();

        assertThrows(NullPointerException.class, () -> productDao.update(id, product));
    }

    @Test
    void deleteById_ShouldDeleteSuccessfully_WhenExists() {
        Long id = 1L;

        when(jdbcTemplate.update(anyString(), eq(id)))
                .thenReturn(1);

        int rowsDeleted = productDao.deleteById(id);

        assertEquals(1, rowsDeleted);
    }

    @Test
    void deleteById_ShouldThrowException_WhenProductNotFound() {
        Long id = 1L;

        when(jdbcTemplate.update(anyString(), eq(id)))
                .thenReturn(0);

        assertThrows(DataNotFoundException.class, () -> productDao.deleteById(id));
    }
}