package org;

import org.config.AppConfig;
import org.dao.CategoryDao;
import org.dao.InventoryDao;
import org.dao.LocationDao;
import org.dao.ProductDao;
import org.dto.LoginRequest;
import org.dto.RegisterRequest;
import org.env.DotenvPropertySource;
import org.exception.InvalidValidatorException;
import org.model.Category;
import org.model.Inventory;
import org.model.Location;
import org.model.Product;
import org.service.EmployeeService;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;

import java.math.BigDecimal;
import java.util.List;

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

        EmployeeService employeeService = context.getBean(EmployeeService.class);
        var request = new LoginRequest("", "");
        try {
            System.out.println(employeeService.login(request));
        } catch (InvalidValidatorException e) {
            e.getAllMessage().iterator().forEachRemaining(System.out::println);
        }
    }
}