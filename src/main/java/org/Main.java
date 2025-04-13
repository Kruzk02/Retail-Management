package org;

import org.env.DotenvPropertySource;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.PropertySource;
import org.springframework.jdbc.core.JdbcTemplate;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

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

        var template = context.getBean(JdbcTemplate.class);
        Integer result = template.queryForObject("SELECT 1", Integer.class);
        if (result != null && result > 0) {
            System.out.println("Database is up");
        } else {
            System.out.println("Database is down");
        }
    }
}