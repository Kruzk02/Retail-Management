package org;

import org.config.AppConfig;
import org.env.DotenvPropertySource;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

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

        AuthenticationManager authenticationManager = context.getBean(AuthenticationManager.class);

        try {
            Authentication authentication = new UsernamePasswordAuthenticationToken("bean",1234);
            Authentication authenticated = authenticationManager.authenticate(authentication);

            System.out.println("Authenticated: " + authenticated.isAuthenticated());
            System.out.println("Authorities: " + authenticated.getAuthorities());
        } catch (Exception e) {
            System.out.println("Authentication failed: " + e.getMessage());
        }
    }
}