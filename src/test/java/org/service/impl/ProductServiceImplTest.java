package org.service.impl;

import org.dao.CategoryDao;
import org.dao.ProductDao;
import org.dto.ProductRequest;
import org.exception.DataNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.model.Category;
import org.model.Product;
import org.service.ProductService;
import org.springframework.dao.EmptyResultDataAccessException;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.Collection;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ProductServiceImplTest {

    private ProductDao productDao;
    private CategoryDao categoryDao;
    private ProductService productService;

    private Product product;
    private Category category;

    private ProductRequest productRequest;

    @BeforeEach
    void setUp() {
        productDao = Mockito.mock(ProductDao.class);
        categoryDao = Mockito.mock(CategoryDao.class);
        productService = new ProductServiceImpl(productDao, categoryDao);

        category = Category.builder().name("name").build();
        product = Product.builder()
                .id(1L)
                .name("name")
                .description("description")
                .price(BigDecimal.ONE)
                .categories(List.of(category))
                .created_at(Timestamp.from(Instant.now()))
                .build();

        productRequest = new ProductRequest("name", "description", BigDecimal.ONE, List.of("name"));
    }

    @Test
    void testSave_shouldSuccess() {
        when(categoryDao.findByName("name")).thenReturn(category);
        when(productService.save(productRequest)).thenReturn(product);

        Product saved = productService.save(productRequest);

        assertEquals("name", saved.getName());
        assertEquals("description", saved.getDescription());
        assertEquals(BigDecimal.ONE, saved.getPrice());
        assertEquals(List.of(category), saved.getCategories());
    }

    @Test
    void testSave_shouldThrowException() { // I don't know what to need test here

    }

    @Test
    void testFindById_shouldSuccess() {
        when(productService.findById(1L)).thenReturn(product);

        Product result = productService.findById(1L);

        assertNotNull(result);
        assertEquals(product.getId(), result.getId());
    }

    @Test
    void testFindById_shouldNotFound() {
        when(productService.findById(0L)).thenThrow(new EmptyResultDataAccessException(1));

        Product result = productService.findById(product.getId());

        assertNull(result);
    }

    @Test
    void testUpdate_shouldSuccess() {
        when(productDao.isProductExists(1L)).thenReturn(true);
        when(productDao.findById(1L)).thenReturn(product);
        when(productService.update(1L, productRequest)).thenReturn(product);

        Product updated = productService.update(1L, productRequest);

        assertEquals("name", updated.getName());
        assertEquals("description", updated.getDescription());
        assertEquals(BigDecimal.ONE, updated.getPrice());
    }

    @Test
    void testUpdate_shouldNotFound() {
        when(productDao.isProductExists(2L)).thenReturn(false);

        Exception ex = assertThrows(DataNotFoundException.class, () -> productService.update(1L, productRequest));

        assertTrue(ex.getMessage().contains("Product not found"));
    }


    @Test
    void testDeleteById_shouldSuccess() {
        when(productDao.isProductExists(1L)).thenReturn(true);
        when(productDao.deleteById(1L)).thenReturn(1);

        int result = productService.deleteById(1L);

        assertEquals(1, result);
        verify(productDao).deleteById(1L);
    }

    @Test
    void testDeleteById_shouldNotFound() {
        when(productDao.isProductExists(2L)).thenReturn(false);

        Exception ex = assertThrows(DataNotFoundException.class, () -> {
            productService.deleteById(2L);
        });

        assertTrue(ex.getMessage().contains("Product not found"));
    }
}