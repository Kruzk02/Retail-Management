package org.config;

import org.dao.CategoryDao;
import org.dao.UserDao;
import org.dao.impl.CategoryDaoImpl;
import org.dao.impl.UserDaoImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

@Configuration
public class DaoConfig {

    @Bean public UserDao userDao(JdbcTemplate jdbcTemplate) {
        return new UserDaoImpl(jdbcTemplate);
    }

    @Bean public CategoryDao categoryDao(JdbcTemplate jdbcTemplate) {
        return new CategoryDaoImpl(jdbcTemplate);
    }
}
