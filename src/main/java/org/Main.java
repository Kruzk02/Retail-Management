package org;

import org.config.AppConfig;
import org.dao.CategoryDao;
import org.dao.InventoryDao;
import org.dao.LocationDao;
import org.dao.ProductDao;
import org.env.DotenvPropertySource;
import org.model.Category;
import org.model.Inventory;
import org.model.Location;
import org.model.Product;
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

        var inventoryDao = context.getBean(InventoryDao.class);
        var productDao = context.getBean(ProductDao.class);
        var locationDao = context.getBean(LocationDao.class);
        var categoryDao = context.getBean(CategoryDao.class);

        Location location = locationDao.save(Location.builder().name("location").build());
        Category category = categoryDao.save(Category.builder().name("category").build());

        Product product = productDao.save(Product.builder()
                .price(BigDecimal.TWO)
                .description("description")
                .name("product")
                .categories(List.of(category))
                .build());

        var inventory = inventoryDao.save(Inventory.builder()
                .product(product)
                .location(location)
                .quantity(24)
                .build());
        System.out.println(inventory.toString());
    }
}