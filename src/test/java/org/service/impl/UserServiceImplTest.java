package org.service.impl;

import org.dao.UserDao;
import org.dto.LoginRequest;
import org.dto.RegisterRequest;
import org.exception.UsernameOrEmailAlreadyExistsException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.model.User;
import org.service.UserService;
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

class UserServiceImplTest {

    private UserDao userDao;
    private PasswordEncoder passwordEncoder;
    private AuthenticationManager authenticationManager;
    private UserService userService;

    @BeforeEach
    void setUp() {
        userDao = Mockito.mock(UserDao.class);
        passwordEncoder = Mockito.mock(PasswordEncoder.class);
        authenticationManager = Mockito.mock(AuthenticationManager.class);
        userService = new UserServiceImpl(userDao, passwordEncoder, authenticationManager);
    }

    @Test
    void register_shouldRegisterUser_whenUsernameAndEmailAreUnique() {
        RegisterRequest request = new RegisterRequest("test1234", "test@gmail.com", "password");

        when(userDao.isUsernameOrEmailExists(request.username(), request.email())).thenReturn(false);
        when(passwordEncoder.encode(request.password())).thenReturn("hashedPassword");

        User expectedUser = User.builder()
                .username("test1234")
                .email("test@gmail.com")
                .password("hashedPassword")
                .build();

        when(userDao.register(any(User.class))).thenReturn(expectedUser);

        User result = userService.register(request);

        assertEquals(expectedUser, result);

        verify(userDao).isUsernameOrEmailExists("test1234", "test@gmail.com");
        verify(passwordEncoder).encode("password");
        verify(userDao).register(any(User.class));
    }

    @Test
    void register_shouldThrowException_whenUsernameOrEmailExists() {
        RegisterRequest request = new RegisterRequest("test1234", "test@gmail.com", "password");

        when(userDao.isUsernameOrEmailExists(request.username(), request.email())).thenReturn(true);

        assertThrows(UsernameOrEmailAlreadyExistsException.class, () -> userService.register(request));

        verify(userDao).isUsernameOrEmailExists("test1234", "test@gmail.com");
        verifyNoMoreInteractions(userDao);
        verifyNoInteractions(passwordEncoder);
    }

    @Test
    void login_shouldAuthenticationSuccessfully() {
        LoginRequest request = new LoginRequest("test", "password");

        Authentication mockAuthentication = mock(Authentication.class);

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(mockAuthentication);

        Authentication result = userService.login(request);

        assertEquals(mockAuthentication, result);
        assertEquals(mockAuthentication, SecurityContextHolder.getContext().getAuthentication());
        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
    }

    @Test
    void login_shouldThrowBadCredentialsException_whenAuthenticationFails() {
        LoginRequest request = new LoginRequest("test", "password");

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow( new InternalAuthenticationServiceException("Failure"));

        BadCredentialsException exception = assertThrows(BadCredentialsException.class, () -> userService.login(request));
        assertEquals("Invalid username or password", exception.getMessage());
    }
}