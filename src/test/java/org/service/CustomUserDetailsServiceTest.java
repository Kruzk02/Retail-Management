package org.service;

import org.dao.EmployeeDao;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.model.Employee;
import org.service.security.CustomUserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class CustomUserDetailsServiceTest {

    private EmployeeDao employeeDao;
    private CustomUserDetailsService customUserDetailsService;

    @BeforeEach
    void setUp() {
        employeeDao = mock(EmployeeDao.class);
        customUserDetailsService = new CustomUserDetailsService(employeeDao);
    }

    @Test
    public void testLoadUserByUsername_UserExists() {
        Employee employee = Employee.builder().username("testuser").password("password").build();

        when(employeeDao.login("testuser")).thenReturn(employee);

        UserDetails userDetails = customUserDetailsService.loadUserByUsername("testuser");

        assertNotNull(userDetails);
        assertEquals("testuser", userDetails.getUsername());
        assertEquals("password", userDetails.getPassword());
    }

    @Test
    public void testLoadUserByUsername_UserNotFound() {
        when(employeeDao.login("nonexistent")).thenReturn(null);

        assertThrows(UsernameNotFoundException.class, () -> {
            customUserDetailsService.loadUserByUsername("nonexistent");
        });
    }
}