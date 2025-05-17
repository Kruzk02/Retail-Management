package org.dao.impl;

import org.dao.EmployeeDao;
import org.exception.DataNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.model.Employee;
import org.model.Role;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class EmployeeDaoImplTest {

    private JdbcTemplate jdbcTemplate;
    private EmployeeDao employeeDao;

    @BeforeEach
    void setup() {
        jdbcTemplate = Mockito.mock(JdbcTemplate.class);
        employeeDao = new EmployeeDaoImpl(jdbcTemplate);
    }

    @Test
    void testIsUsernameOrEmailExists_ReturnTrue() {
        String username = "testuser";
        String email = "test@gmail.com";

        when(jdbcTemplate.queryForObject(anyString(), eq(Boolean.class), eq(username),eq(email))).thenReturn(true);

        Boolean result = employeeDao.isUsernameOrEmailExists(username, email);
        assertTrue(result);
    }

    @Test
    void testIsUsernameOrEmailExists_ReturnsFalse() {
        String username = "testuser";
        String email = "test@gmail.com";

        when(jdbcTemplate.queryForObject(anyString(), eq(Boolean.class), eq(username), eq(email))).thenReturn(false);


        Boolean result = employeeDao.isUsernameOrEmailExists(username, email);

        assertFalse(result);
    }

    @Test
    void testLogin_success() {
        Role role = Role.builder()
            .id(1L)
            .name("ROLE_ADMIN")
            .privileges(Collections.emptyList())
        .build();

        String username = "test";
        Employee expectedEmployee = Employee.builder()
                .id(1L)
                .username("test")
                .email("test@example.com")
                .password("hashed_password")
                .roles(List.of(role))
                .createdAt(java.time.LocalDateTime.now())
                .build();

        when(jdbcTemplate.queryForObject(
                anyString(),
                any(EmployeeRowMapper.class),
                eq(username)
        )).thenReturn(expectedEmployee);

        Employee result = employeeDao.login(username);

        assertNotNull(result);
        assertEquals(expectedEmployee, result);
        assertEquals(List.of(role),result.getRoles());
        verify(jdbcTemplate).queryForObject(anyString(), any(EmployeeRowMapper.class), eq(username));
    }

    @Test
    void testLogin_notFound() {
        String username = "test";

        when(jdbcTemplate.queryForObject(
                anyString(),
                any(EmployeeRowMapper.class),
                eq(username)
        )).thenThrow(new EmptyResultDataAccessException(1));

        Exception exception = assertThrows(DataNotFoundException.class, () -> employeeDao.login(username));
        assertTrue(exception.getMessage().contains("User not found"));
    }

    @Test
    void testRegister_Success() {
        Employee inputEmployee = Employee.builder()
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

        Employee employee = employeeDao.register(inputEmployee);

        assertNotNull(employee);
        assertEquals(1L, employee.getId());
        assertEquals("testuser", employee.getUsername());
        assertEquals("test@gmail.com", employee.getEmail());
    }

    @Test
    void testRegister_FailureToInsert() {
        Employee inputEmployee = Employee.builder()
                .username("testuser")
                .email("test@gmail.com")
                .password("password")
                .build();

        Mockito.when(jdbcTemplate.update(any(PreparedStatementCreator.class), any(KeyHolder.class)))
                .thenReturn(0);

        assertThrows(IllegalStateException.class, () -> employeeDao.register(inputEmployee));
    }
}