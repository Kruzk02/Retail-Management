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
    void testIsUsernameOrEmailExists_ReturnTrue() {
        String username = "testuser";
        String email = "test@gmail.com";

        when(jdbcTemplate.queryForObject(anyString(), eq(Boolean.class), eq(username),eq(email))).thenReturn(true);

        Boolean result = userDao.isUsernameOrEmailExists(username, email);
        assertTrue(result);
    }

    @Test
    void testIsUsernameOrEmailExists_ReturnsFalse() {
        String username = "testuser";
        String email = "test@gmail.com";

        when(jdbcTemplate.queryForObject(anyString(), eq(Boolean.class), eq(username), eq(email))).thenReturn(false);


        Boolean result = userDao.isUsernameOrEmailExists(username, email);

        assertFalse(result);
    }

    @Test
    void testFindPasswordByUsername_success() {
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

        User result = userDao.findPasswordByUsername(username);

        assertNotNull(result);
        assertEquals(expectedUser, result);
        verify(jdbcTemplate).queryForObject(anyString(), any(UserRowMapper.class), eq(username));
    }

    @Test
    void testFindPasswordByUsername_notFound() {
        String username = "test";

        when(jdbcTemplate.queryForObject(
                anyString(),
                any(UserRowMapper.class),
                eq(username)
        )).thenThrow(new EmptyResultDataAccessException(1));

        Exception exception = assertThrows(DataNotFoundException.class, () -> userDao.findPasswordByUsername(username));
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
}