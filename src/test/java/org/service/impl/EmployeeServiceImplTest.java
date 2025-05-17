package org.service.impl;

import org.dao.EmployeeDao;
import org.dto.LoginRequest;
import org.dto.RegisterRequest;
import org.exception.UsernameOrEmailAlreadyExistsException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.model.Employee;
import org.service.EmployeeService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class EmployeeServiceImplTest {

    private EmployeeDao employeeDao;
    private PasswordEncoder passwordEncoder;
    private AuthenticationManager authenticationManager;
    private EmployeeService employeeService;

    @BeforeEach
    void setUp() {
        employeeDao = Mockito.mock(EmployeeDao.class);
        passwordEncoder = Mockito.mock(PasswordEncoder.class);
        authenticationManager = Mockito.mock(AuthenticationManager.class);
        employeeService = new EmployeeServiceImpl(employeeDao, passwordEncoder, authenticationManager);
    }

    @Test
    void register_shouldRegisterUser_whenUsernameAndEmailAreUnique() {
        RegisterRequest request = new RegisterRequest("test1234", "test@gmail.com", "password");

        when(employeeDao.isUsernameOrEmailExists(request.username(), request.email())).thenReturn(false);
        when(passwordEncoder.encode(request.password())).thenReturn("hashedPassword");

        Employee expectedEmployee = Employee.builder()
                .username("test1234")
                .email("test@gmail.com")
                .password("hashedPassword")
                .build();

        when(employeeDao.register(any(Employee.class))).thenReturn(expectedEmployee);

        Employee result = employeeService.register(request);

        assertEquals(expectedEmployee, result);

        verify(employeeDao).isUsernameOrEmailExists("test1234", "test@gmail.com");
        verify(passwordEncoder).encode("password");
        verify(employeeDao).register(any(Employee.class));
    }

    @Test
    void register_shouldThrowException_whenUsernameOrEmailExists() {
        RegisterRequest request = new RegisterRequest("test1234", "test@gmail.com", "password");

        when(employeeDao.isUsernameOrEmailExists(request.username(), request.email())).thenReturn(true);

        assertThrows(UsernameOrEmailAlreadyExistsException.class, () -> employeeService.register(request));

        verify(employeeDao).isUsernameOrEmailExists("test1234", "test@gmail.com");
        verifyNoMoreInteractions(employeeDao);
        verifyNoInteractions(passwordEncoder);
    }

    @Test
    void login_shouldAuthenticationSuccessfully() {
        LoginRequest request = new LoginRequest("test", "password");

        Authentication mockAuthentication = mock(Authentication.class);

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(mockAuthentication);

        Authentication result = employeeService.login(request);

        assertEquals(mockAuthentication, result);
        assertEquals(mockAuthentication, SecurityContextHolder.getContext().getAuthentication());
        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
    }

    @Test
    void login_shouldThrowBadCredentialsException_whenAuthenticationFails() {
        LoginRequest request = new LoginRequest("test", "password");

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow( new InternalAuthenticationServiceException("Failure"));

        BadCredentialsException exception = assertThrows(BadCredentialsException.class, () -> employeeService.login(request));
        assertEquals("Invalid username or password", exception.getMessage());
    }
}