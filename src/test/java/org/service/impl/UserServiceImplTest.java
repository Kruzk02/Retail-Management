package org.service.impl;

import org.dao.UserDao;
import org.dto.RegisterRequest;
import org.exception.UsernameOrEmailAlreadyExistsException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.model.User;
import org.service.UserService;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class UserServiceImplTest {

    private UserDao userDao;
    private PasswordEncoder passwordEncoder;
    private UserService userService;

    @BeforeEach
    void setUp() {
        userDao = Mockito.mock(UserDao.class);
        passwordEncoder = Mockito.mock(PasswordEncoder.class);
        userService = new UserServiceImpl(userDao, passwordEncoder);
    }

    @Test
    void register_shouldRegisterUser_whenUsernameAndEmailAreUnique() {
        RegisterRequest request = new RegisterRequest("test", "test@gmail.com", "password");

        when(userDao.isUsernameOrEmailExists(request.username(), request.email())).thenReturn(false);
        when(passwordEncoder.encode(request.password())).thenReturn("hashedPassword");

        User expectedUser = User.builder()
                .username("test")
                .email("test@gmail.com")
                .password("hashedPassword")
                .build();

        when(userDao.register(any(User.class))).thenReturn(expectedUser);

        User result = userService.register(request);

        assertEquals(expectedUser, result);

        verify(userDao).isUsernameOrEmailExists("test", "test@gmail.com");
        verify(passwordEncoder).encode("password");
        verify(userDao).register(any(User.class));
    }

    @Test
    void register_shouldThrowException_whenUsernameOrEmailExists() {
        RegisterRequest request = new RegisterRequest("test", "test@gmail.com", "password");

        when(userDao.isUsernameOrEmailExists(request.username(), request.email())).thenReturn(true);

        assertThrows(UsernameOrEmailAlreadyExistsException.class, () -> userService.register(request));

        verify(userDao).isUsernameOrEmailExists("test", "test@gmail.com");
        verifyNoMoreInteractions(userDao);
        verifyNoInteractions(passwordEncoder);
    }
}