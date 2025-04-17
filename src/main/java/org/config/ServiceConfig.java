package org.config;

import org.dao.UserDao;
import org.service.CustomUserDetailsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.userdetails.UserDetailsService;

@Configuration
public class ServiceConfig {

    @Bean public UserDetailsService userDetailsService(UserDao userDao) {
        return new CustomUserDetailsService(userDao);
    }

}
