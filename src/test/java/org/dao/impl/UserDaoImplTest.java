package org.dao.impl;

import org.dao.UserDao;
import org.exception.DataNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.model.User;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import java.util.HashMap;
import java.util.Map;

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

    @Test
    void testRegister_Success() {
        User inputUser = User.builder()
                .username("testuser")
                .email("test@gmail.com")
                .password("password")
                .build();

        KeyHolder keyHolder = new GeneratedKeyHolder();
        Map<String, Object> keys = new HashMap<>();
        keys.put("GENERATED_KEY", 1L);
        keyHolder.getKeyList().add(keys);

        ArgumentCaptor<PreparedStatementCreator> captor = ArgumentCaptor.forClass(PreparedStatementCreator.class);

        Mockito.when(jdbcTemplate.update(captor.capture(), any(KeyHolder.class)))
                .thenAnswer(invocation -> {
                    KeyHolder kh = invocation.getArgument(1);
                    kh.getKeyList().add(Map.of("id", 1L));
                    return 1;
                });

        User user = userDao.register(inputUser);

        assertNotNull(user);
        assertEquals(1L, user.getId());
        assertEquals("testuser", user.getUsername());
        assertEquals("test@gmail.com", user.getEmail());
    }

    @Test
    void testRegister_FailureToInsert() {
        User inputUser = User.builder()
                .username("testuser")
                .email("test@gmail.com")
                .password("password")
                .build();

        Mockito.when(jdbcTemplate.update(any(PreparedStatementCreator.class), any(KeyHolder.class)))
                .thenReturn(0);

        assertThrows(IllegalStateException.class, () -> userDao.register(inputUser));
    }


    @Test
    void testRegister_DataAccessException() {
        User inputUser = User.builder()
                .username("testuser")
                .email("test@gmail.com")
                .password("password")
                .build();

        Mockito.when(jdbcTemplate.update(any(PreparedStatementCreator.class), any(KeyHolder.class)))
                .thenThrow(new DataAccessResourceFailureException("Database unavailable"));

        RuntimeException thrown = assertThrows(RuntimeException.class, () -> userDao.register(inputUser));
        assertTrue(thrown.getMessage().contains("Database error"));
    }
}