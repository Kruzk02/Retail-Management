package org.config;

import org.dao.UserDao;
import org.service.UserService;
import org.service.impl.UserServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class ServiceConfig {

    @Bean
    public UserService userService(UserDao userDao, PasswordEncoder passwordEncoder) {
        return new UserServiceImpl(userDao, passwordEncoder);
    }
}
