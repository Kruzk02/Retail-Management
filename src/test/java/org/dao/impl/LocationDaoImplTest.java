package org.dao.impl;

import org.dao.LocationDao;
import org.exception.DataNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.model.Location;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class LocationDaoImplTest {

    private JdbcTemplate jdbcTemplate;
    private LocationDao locationDao;

    @BeforeEach
    void setUp() {
        jdbcTemplate = Mockito.mock(JdbcTemplate.class);
        locationDao = new LocationDaoImpl(jdbcTemplate);
    }

    @Test
    void testLocationsExists_success() {
        String name = "AA";

        when(jdbcTemplate.queryForObject(anyString(), eq(Boolean.class), eq(1L), eq(name))).thenReturn(true);

        Boolean result = locationDao.isLocationExist(1L, name);
        assertTrue(result);
    }

    @Test
    void testLocationsExists_failed() {
        String name = "AA";

        when(jdbcTemplate.queryForObject(anyString(), eq(Boolean.class), eq(0L), eq(name))).thenReturn(false);

        Boolean result = locationDao.isLocationExist(0L, name);
        assertFalse(result);
    }

    @Test
    void testSave_success() {
        Location inputLocation = Location.builder().id(1L).name("name").build();

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
        Location location = locationDao.save(inputLocation);

        assertNotNull(location);
        assertEquals(1L, location.getId());
        assertEquals("name", location.getName());
    }

    @Test
    void testSave_failed() {
        Location inputLocation = Location.builder().id(1L).name("name").build();

        when(jdbcTemplate.update(any(PreparedStatementCreator.class), any(KeyHolder.class))).thenReturn(0);

        assertThrows(IllegalStateException.class, () -> locationDao.save(inputLocation));
    }

    @Test
    void testFindById_success() {
        Location location = Location.builder().id(1L).name("name").build();

        when(jdbcTemplate.queryForObject(anyString(), any(RowMapper.class), eq(1L))).thenReturn(location);

        Location result = locationDao.findById(1L);

        assertNotNull(result);
        assertEquals(location, result);
        verify(jdbcTemplate).queryForObject(anyString(), any(RowMapper.class),eq(1L));
    }

    @Test
    void testFindById_Failed() {
        when(jdbcTemplate.queryForObject(anyString(), any(RowMapper.class), eq(0L))).thenThrow(new EmptyResultDataAccessException(1));

        Exception exception = assertThrows(DataNotFoundException.class, () -> locationDao.findById(0L));
        assertTrue(exception.getMessage().contains("Location not found with id"));
    }

    @Test
    void testFindByName_success() {
        Location location = Location.builder().id(1L).name("name").build();

        when(jdbcTemplate.queryForObject(anyString(), any(RowMapper.class), eq("name"))).thenReturn(location);

        Location result = locationDao.findByName("name");

        assertNotNull(result);
        assertEquals(location, result);
        verify(jdbcTemplate).queryForObject(anyString(), any(RowMapper.class),eq("name"));
    }

    @Test
    void testFindByName_Failed() {
        when(jdbcTemplate.queryForObject(anyString(), any(RowMapper.class), eq(""))).thenThrow(new EmptyResultDataAccessException(1));

        Exception exception = assertThrows(DataNotFoundException.class, () -> locationDao.findByName(""));
        assertTrue(exception.getMessage().contains("Location not found with name"));
    }

    @Test
    void testDeleteById_success() {
        Long id = 1L;

        when(jdbcTemplate.update(anyString(), eq(id))).thenReturn(1);

        int rowDelete = locationDao.deleteById(id);
        assertEquals(1, rowDelete);
    }

    @Test
    void testDeleteById_failed() {
        Long id = 1L;

        when(jdbcTemplate.update(anyString(), eq(id))).thenReturn(0);

        assertThrows(DataNotFoundException.class, () -> locationDao.deleteById(id));
    }

}