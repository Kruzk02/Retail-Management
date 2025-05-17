package org.config;

import org.dao.*;
import org.dao.impl.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

@Configuration
public class DaoConfig {

    @Bean public EmployeeDao employeeDao(JdbcTemplate jdbcTemplate) {
        return new EmployeeDaoImpl(jdbcTemplate);
    }

    @Bean public CategoryDao categoryDao(JdbcTemplate jdbcTemplate) {
        return new CategoryDaoImpl(jdbcTemplate);
    }

    @Bean public ProductDao productDao(JdbcTemplate jdbcTemplate) {
        return new ProductDaoImpl(jdbcTemplate);
    }

    @Bean public LocationDao locationDao(JdbcTemplate jdbcTemplate) {
        return new LocationDaoImpl(jdbcTemplate);
    }

    @Bean public InventoryDao inventoryDao(JdbcTemplate jdbcTemplate) {
        return new InventoryDaoImpl(jdbcTemplate);
    }

    @Bean public SupplierDao supplierDao(JdbcTemplate jdbcTemplate) {
        return new SupplierDaoImpl(jdbcTemplate);
    }
}
