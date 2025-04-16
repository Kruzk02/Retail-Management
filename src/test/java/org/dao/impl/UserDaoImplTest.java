package org.dao.impl;

import org.dao.UserDao;
import org.exception.DataNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.model.User;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserDaoImplTest {

    private JdbcTemplate jdbcTemplate;
    private UserDao userDao;

    @BeforeEach
    void setup() {
        jdbcTemplate = Mockito.mock(JdbcTemplate.class);
        userDao = new UserDaoImpl(jdbcTemplate);
    }

    @Test
    void testFindByUsername_success() {
        String username = "test";
        User expectedUser = User.builder()
                .id(1L)
                .username("test")
                .email("test@example.com")
                .password("hashed_password")
                .createdAt(java.time.LocalDateTime.now())
                .build();

        when(jdbcTemplate.queryForObject(
                anyString(),
                any(UserRowMapper.class),
                eq(username)
        )).thenReturn(expectedUser);

        User result = userDao.findByUsername(username);

        assertNotNull(result);
        assertEquals(expectedUser, result);
        verify(jdbcTemplate).queryForObject(anyString(), any(UserRowMapper.class), eq(username));
    }

    @Test
    void testFindByUsername_notFound() {
        String username = "test";

        when(jdbcTemplate.queryForObject(
                anyString(),
                any(UserRowMapper.class),
                eq(username)
        )).thenThrow(new EmptyResultDataAccessException(1));

        Exception exception = assertThrows(DataNotFoundException.class, () -> userDao.findByUsername(username));
        assertTrue(exception.getMessage().contains("User not found"));
    }
}