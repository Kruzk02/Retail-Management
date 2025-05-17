package org.config;

import org.dao.*;
import org.service.*;
import org.service.impl.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class ServiceConfig {

    @Bean
    public EmployeeService employeeService(EmployeeDao employeeDao, PasswordEncoder passwordEncoder, AuthenticationManager authenticationManager) {
        return new EmployeeServiceImpl(employeeDao, passwordEncoder, authenticationManager);
    }

    @Bean
    public CategoryService categoryService(CategoryDao categoryDao) {
        return new CategoryServiceImpl(categoryDao);
    }

    @Bean
    public ProductService productService(ProductDao productDao, CategoryDao categoryDao) {
        return new ProductServiceImpl(productDao, categoryDao);
    }

    @Bean
    public LocationService locationService(LocationDao locationDao) {
        return new LocationServiceImpl(locationDao);
    }

    @Bean
    public InventoryService inventoryService(InventoryDao inventoryDao, ProductDao productDao, LocationDao locationDao) {
        return new InventoryServiceImpl(inventoryDao, productDao, locationDao);
    }

    @Bean
    public SupplierService supplierService(SupplierDao supplierDao) {
        return new SupplierServiceImpl(supplierDao);
    }
}
