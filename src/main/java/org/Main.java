package org;

import org.config.AppConfig;
import org.dto.LoginRequest;
import org.dto.RegisterRequest;
import org.env.DotenvPropertySource;
import org.model.User;
import org.service.UserService;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class Main {
    public static void main(String[] args) {
        var context = new AnnotationConfigApplicationContext();

        try {
            DotenvPropertySource dotenv = new DotenvPropertySource("dotenv");
            ConfigurableEnvironment env = context.getEnvironment();
            env.getPropertySources().addFirst(dotenv);
        } catch (Exception e) {
            throw new RuntimeException("Failed to load .env", e);
        }

        context.register(AppConfig.class);
        context.refresh();

        UserService userService = context.getBean(UserService.class);
//        User user = userService.register(new RegisterRequest("username", "email@gmail.com", "password"));
//        System.out.println(user);
        Authentication authentication = userService.login(new LoginRequest("username", "password"));
        System.out.println(authentication.isAuthenticated());
    }
}