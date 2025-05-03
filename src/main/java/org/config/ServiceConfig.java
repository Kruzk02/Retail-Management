package org.config;

import org.dao.CategoryDao;
import org.dao.ProductDao;
import org.dao.UserDao;
import org.service.CategoryService;
import org.service.ProductService;
import org.service.UserService;
import org.service.impl.CategoryServiceImpl;
import org.service.impl.ProductServiceImpl;
import org.service.impl.UserServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class ServiceConfig {

    @Bean
    public UserService userService(UserDao userDao, PasswordEncoder passwordEncoder, AuthenticationManager authenticationManager) {
        return new UserServiceImpl(userDao, passwordEncoder, authenticationManager);
    }

    @Bean
    public CategoryService categoryService(CategoryDao categoryDao) {
        return new CategoryServiceImpl(categoryDao);
    }

    @Bean
    public ProductService productService(ProductDao productDao, CategoryDao categoryDao) {
        return new ProductServiceImpl(productDao, categoryDao);
    }
}
