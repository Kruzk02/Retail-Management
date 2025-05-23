package org.service.impl;

import org.dao.CategoryDao;
import org.dao.ProductDao;
import org.dto.ProductRequest;
import org.exception.DataNotFoundException;
import org.exception.InvalidValidatorException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.model.Category;
import org.model.Product;
import org.service.ProductService;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.validation.Errors;
import org.validators.ProductRequestValidator;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class ProductServiceImplTest {

    private ProductDao productDao;
    private CategoryDao categoryDao;
    private ProductRequestValidator productRequestValidator;
    private ProductService productService;

    private Product product;
    private Category category;

    private ProductRequest productRequest;

    @BeforeEach
    void setUp() {
        productDao = Mockito.mock(ProductDao.class);
        categoryDao = Mockito.mock(CategoryDao.class);
        productRequestValidator = Mockito.mock(ProductRequestValidator.class);
        productService = new ProductServiceImpl(productDao, categoryDao, productRequestValidator);

        category = Category.builder().name("name").build();
        product = Product.builder()
                .id(1L)
                .name("name")
                .description("description")
                .price(BigDecimal.ONE)
                .categories(List.of(category))
                .created_at(LocalDateTime.now())
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
    void testSave_shouldThrowExceptionWhenValidationFails() {
        doAnswer(invocation -> {
            Errors errors = invocation.getArgument(1);
            errors.rejectValue("name", "invalid", "Product name must not be empty");
            errors.rejectValue( "description", "invalid", "Product description must not be empty");
            errors.rejectValue( "categories", "invalid", "Categories must not be empty");
            errors.rejectValue( "price", "invalid", "Price must not be empty");
            return null;
        }).when(productRequestValidator).validate(eq(productRequest), any(Errors.class));

        InvalidValidatorException exception = assertThrows(InvalidValidatorException.class,
                () -> productService.save(productRequest));

        List<String> message = exception.getAllMessage();
        assertEquals("Product name must not be empty", message.getFirst());
        assertEquals("Product description must not be empty", message.get(1));
        assertEquals("Categories must not be empty", message.get(2));
        assertEquals("Price must not be empty", message.getLast());
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
    void testUpdate_shouldThrowExceptionWhenValidationFails() {
        doAnswer(invocation -> {
            Errors errors = invocation.getArgument(1);
            errors.rejectValue("name", "invalid", "Product name must not be empty");
            errors.rejectValue( "description", "invalid", "Product description must not be empty");
            errors.rejectValue( "categories", "invalid", "Categories must not be empty");
            errors.rejectValue( "price", "invalid", "Price must not be empty");
            return null;
        }).when(productRequestValidator).validate(eq(productRequest), any(Errors.class));

        InvalidValidatorException exception = assertThrows(InvalidValidatorException.class,
                () -> productService.update(1L, productRequest));

        List<String> message = exception.getAllMessage();
        assertEquals("Product name must not be empty", message.getFirst());
        assertEquals("Product description must not be empty", message.get(1));
        assertEquals("Categories must not be empty", message.get(2));
        assertEquals("Price must not be empty", message.getLast());
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