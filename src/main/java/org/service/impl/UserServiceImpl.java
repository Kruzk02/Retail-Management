package org.service.impl;

import lombok.AllArgsConstructor;
import org.dao.UserDao;
import org.dto.RegisterRequest;
import org.exception.UsernameOrEmailAlreadyExistsException;
import org.model.User;
import org.service.UserService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@AllArgsConstructor
@Service
public class UserServiceImpl implements UserService {

    private final UserDao userDao;
    private final PasswordEncoder passwordEncoder;

    @Override
    public User register(RegisterRequest request) {

        boolean isExists = userDao.isUsernameOrEmailExists(request.username(), request.email());
        if (isExists) {
            throw new UsernameOrEmailAlreadyExistsException("Username or Email already existing.");
        }

        String hashedPassword = passwordEncoder.encode(request.password());
        return userDao.register(User.builder()
            .username(request.username())
            .email(request.email())
            .password(hashedPassword)
            .build());
    }
}
