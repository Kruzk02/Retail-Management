package org.dao.impl;

import org.dao.CategoryDao;
import org.exception.DataNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.model.Category;
import org.springframework.dao.EmptyResultDataAccessException;
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
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class CategoryDaoImplTest {

    private JdbcTemplate jdbcTemplate;
    private CategoryDao categoryDao;

    @BeforeEach
    void setUp() {
        jdbcTemplate = Mockito.mock(JdbcTemplate.class);
        categoryDao = new CategoryDaoImpl(jdbcTemplate);
    }

    @Test
    void testIsCategoryExists_returnTrue() {
        String name = "Books";

        when(jdbcTemplate.queryForObject(anyString(), eq(Boolean.class), eq(1L),  eq(name))).thenReturn(true);

        Boolean result = categoryDao.isCategoryExists(1L,name);
        assertTrue(result);
    }

    @Test
    void testIsCategoryExists_returnFalse() {
        String name = "Books";

        when(jdbcTemplate.queryForObject(anyString(), eq(Boolean.class), eq(0L), eq(name))).thenReturn(false );

        Boolean result = categoryDao.isCategoryExists(0L, name);
        assertFalse(result);
    }

    @Test
    void testSave_success() {
        Category inputCategory = Category.builder()
                .id(1L)
                .name("name")
                .build();

        KeyHolder keyHolder = new GeneratedKeyHolder();
        Map<String, Object> keys = new HashMap<>();
        keys.put("GENERATED_KEYS", 1L);
        keyHolder.getKeyList().add(keys);

        ArgumentCaptor<PreparedStatementCreator> captor = ArgumentCaptor.forClass(PreparedStatementCreator.class);

        Mockito.when(jdbcTemplate.update(captor.capture(), any(KeyHolder.class)))
                .thenAnswer(invocation -> {
                    KeyHolder kh = invocation.getArgument(1);
                    kh.getKeyList().add(Map.of("id", 1L));
                    return 1;
                });
        Category category = categoryDao.save(inputCategory);

        assertNotNull(category);
        assertEquals(1L, category.getId());
        assertEquals("name", category.getName());
    }

    @Test
    void testSave_FailureToInsert() {
        Category input = Category.builder()
                .id(1L)
                .name("name")
                .build();

        Mockito.when(jdbcTemplate.update(any(PreparedStatementCreator.class), any(KeyHolder.class))).thenReturn(0);

        assertThrows(IllegalStateException.class, () -> categoryDao.save(input));
    }

    @Test
    void findCategoryByProductId_success() {
        List<Category> expect = List.of(Category.builder().id(1L).name("name").build());

        when(jdbcTemplate.query(anyString(), any(RowMapper.class), eq(1L))).thenReturn(expect);

        List<Category> categories = categoryDao.findCategoryByProductId(1L);

        assertNotNull(categories);
        assertEquals(expect, categories);
        verify(jdbcTemplate).query(anyString(), any(RowMapper.class), eq(1L));
    }

    @Test
    void findCategoryByProductId_notFound() {
        when(jdbcTemplate.query(anyString(), any(RowMapper.class), eq(1L))).thenThrow(new DataNotFoundException("Product not found with id: " + 1L));

        Exception exception = assertThrows(DataNotFoundException.class, () -> categoryDao.findCategoryByProductId(1L));
        assertTrue(exception.getMessage().contains("Product not found with id"));
    }

    @Test
    void testFindByName_success() {
        Category expect = Category.builder()
                .id(1L)
                .name("name")
                .build();

        when(jdbcTemplate.queryForObject(anyString(), any(RowMapper.class), eq("name"))).thenReturn(expect);

        Category category = categoryDao.findByName("name");

        assertNotNull(category);
        assertEquals(expect, category);
        verify(jdbcTemplate).queryForObject(anyString(), any(RowMapper.class), eq("name"));
    }

    @Test
    void testFIndByName_notFound() {

        when(jdbcTemplate.queryForObject(anyString(), any(RowMapper.class), eq("name"))).thenThrow(new EmptyResultDataAccessException(1));

        Exception exception = assertThrows(DataNotFoundException.class, () -> categoryDao.findByName("name"));
        assertTrue(exception.getMessage().contains("Category not found with name"));
    }

    @Test
    void testDeleteById_success() {
        Long id = 1L;

        when(jdbcTemplate.update(anyString(), eq(id))).thenReturn(1);

        int rowDelete = categoryDao.deleteById(id);
        assertEquals(1, rowDelete);
    }

    @Test
    void testDeleteById_notFound() {
        Long id = 1L;

        when(jdbcTemplate.update(anyString(), eq(id))).thenReturn(0);

        assertThrows(DataNotFoundException.class, () -> categoryDao.deleteById(id));
    }
}