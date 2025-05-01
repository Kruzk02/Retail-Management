package org.service.impl;

import org.dao.CategoryDao;
import org.dao.impl.CategoryDaoImpl;
import org.exception.DataNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.model.Category;
import org.service.CategoryService;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class CategoryServiceImplTest {

    private CategoryDao categoryDao;
    private CategoryService categoryService;

    private Category category;
    @BeforeEach
    void setUp() {
        categoryDao = Mockito.mock(CategoryDaoImpl.class);
        categoryService = new CategoryServiceImpl(categoryDao);

        category = Category.builder()
                .id(1L)
                .name("name")
                .build();
    }

    @Test
    void testFindByName_success() {

        when(categoryService.findByName(category.getName())).thenReturn(category);

        Category existingCategory = categoryDao.findByName(category.getName());

        assertNotNull(existingCategory);
        assertEquals(category.getName(), existingCategory.getName());
        verify(categoryDao).findByName(category.getName());
    }

    @Test
    void testFindByName_notFound() {
        when(categoryService.findByName("NO")).thenThrow(new EmptyResultDataAccessException(1));

        Category existingCategory = categoryDao.findByName(category.getName());

        assertNull(existingCategory);
    }

    @Test
    void testSave_success() {
        when(categoryDao.isCategoryExists(null, "name")).thenReturn(true);
        when(categoryService.save("name")).thenReturn(category);

        Category savedCategory = categoryService.save("name");

        assertNotNull(savedCategory);
        assertEquals(category, savedCategory);
        assertEquals(category.getName(), savedCategory.getName());
        verify(categoryDao).save(any(Category.class));
    }

    @Test
    void testSave_throwException() {

        when(categoryDao.isCategoryExists(null, "name")).thenReturn(false);

        DataNotFoundException exception = assertThrows(DataNotFoundException.class, () -> categoryService.save("name"));
        assertTrue(exception.getMessage().contains("Category not found with a name:"));
    }

    @Test
    void testDeleteById_success() {

        when(categoryDao.isCategoryExists(1L, null)).thenReturn(true);
        when(categoryService.deleteById(1L)).thenReturn(1);

        int result = categoryService.deleteById(1L);

        assertEquals(1, result);
        verify(categoryDao).deleteById(1L);
    }

    @Test
    void testDeleteById_throwException() {

        when(categoryDao.isCategoryExists(1L, null)).thenReturn(false);

        DataNotFoundException exception = assertThrows(DataNotFoundException.class, () -> categoryService.deleteById(1L));
        assertTrue(exception.getMessage().contains("Category not found with a id:"));
    }
}