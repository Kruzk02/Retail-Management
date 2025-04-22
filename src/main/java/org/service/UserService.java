package org.service;

import org.dto.LoginRequest;
import org.dto.RegisterRequest;
import org.model.User;
import org.springframework.security.core.Authentication;

/**
 * User service class responsible for user related operations such as registration, login and retrieval
 */
public interface UserService {
    User register(RegisterRequest request);
    Authentication login(LoginRequest request);
}
