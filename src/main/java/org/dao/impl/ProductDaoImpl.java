package org.dao.impl;

import lombok.AllArgsConstructor;
import org.dao.ProductDao;
import org.exception.DataNotFoundException;
import org.model.Category;
import org.model.Product;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.sql.PreparedStatement;
import java.util.*;
import java.util.stream.Collectors;

@AllArgsConstructor
@Repository
public class ProductDaoImpl implements ProductDao {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public Boolean isProductExists(Long id) {
        String sql = "SELECT EXISTS(SELECT 1 FROM products WHERE id = ?)";
        return jdbcTemplate.queryForObject(sql, Boolean.class, id);
    }

    @Transactional(
            propagation = Propagation.REQUIRED,
            rollbackFor = Exception.class,
            isolation = Isolation.READ_COMMITTED
    )
    @Override
    public Product save(Product product) {
        String sql = "INSERT INTO products(name, description, price, stock_quantity) VALUES(?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();

        int rowsAffected = jdbcTemplate.update(conn -> {
            PreparedStatement ps = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS);
            ps.setString(1, product.getName());
            ps.setString(2, product.getDescription());
            ps.setBigDecimal(3, product.getPrice());
            return ps;
        }, keyHolder);

        if (rowsAffected > 0) {
            product.setId((Long) Objects.requireNonNull(keyHolder.getKeys()).get("id"));

            for (Category category : product.getCategories()) {
                String pcSql = "INSERT INTO products_categories(product_id, category_id) VALUES (?, ?)";
                jdbcTemplate.update(pcSql, product.getId(), category.getId());
            }

            return product;
        } else {
            throw new IllegalStateException("Failed to insert product into database");
        }
    }

    //Basic thing for now.
    @Override
    public Product findById(Long id) {
        try {
            String sql = "SELECT name, description, price, stock_quantity FROM products WHERE id = ?";
            return jdbcTemplate.queryForObject(sql, (rs, rowNum) ->
                Product.builder()
                        .id(id)
                        .name(rs.getString("name"))
                        .description(rs.getString("description"))
                        .price(rs.getBigDecimal("price"))
                        .build()
            ,id);
        } catch (EmptyResultDataAccessException e) {
            throw new DataNotFoundException("Product not found with a id: " + id);
        }
    }

    @Transactional
    private void updateProductCategories(Product product) {
        String sql = "SELECT c.id, c.name FROM categories c " +
                "JOIN products_categories pc ON c.id = pc.category_id " +
                "WHERE pc.product_id = ?";

        List<Category> existingCategories = jdbcTemplate.query(sql, (rs, rowNum) ->
                        Category.builder()
                                .id(rs.getLong("id"))
                                .name(rs.getString("name"))
                                .build()
                , product.getId());

        Set<Long> existingCategoryIds = existingCategories.stream().map(Category::getId).collect(Collectors.toSet());
        Set<Long> newCategoryIds = product.getCategories().stream().map(Category::getId).collect(Collectors.toSet());

        Set<Long> toAdd = new HashSet<>(newCategoryIds);
        toAdd.removeAll(existingCategoryIds);

        Set<Long> toRemove = new HashSet<>(existingCategoryIds);
        toRemove.removeAll(newCategoryIds);

        for (Long categoryId : toRemove) {
            jdbcTemplate.update("DELETE FROM products_categories WHERE product_id = ? AND category_id = ?",
                    product.getId(), categoryId);
        }

        for (Long categoryId : toAdd) {
            jdbcTemplate.update("INSERT INTO products_categories (product_id, category_id) VALUES (?, ?)",
                    product.getId(), categoryId);
        }
    }

    @Transactional
    @Override
    public Product update(Long id, Product product) {
        StringBuilder sb = new StringBuilder("UPDATE products SET ");
        List<Object> params = new ArrayList<>();

        if (product.getName() != null) {
            sb.append("name = ?, ");
            params.add(product.getName());
        }

        if (product.getDescription() != null) {
            sb.append("description = ?, ");
            params.add(product.getDescription());
        }

        if (product.getPrice() != null) {
            sb.append("price = ?, ");
            params.add(product.getPrice());
        }

        if (!product.getCategories().isEmpty()) {
            updateProductCategories(product);
        }

        if (params.isEmpty()) {
            throw new IllegalArgumentException("No field to update");
        }

        if (!sb.isEmpty()) {
            sb.setLength(sb.length() - 2);
        }

        sb.append(" WHERE id = ?");
        params.add(id);

        String sql = sb.toString();
        int rowAffected = jdbcTemplate.update(sql, params.toArray());

        return rowAffected > 0 ? product : null;
    }

    @Transactional
    @Override
    public int deleteById(Long id) {
        String sql = "DELETE FROM products WHERE id = ?";
        int rows = jdbcTemplate.update(sql,id);
        if (rows == 0) {
            throw new DataNotFoundException("Product not found with a id: " + id);
        }
        return rows;
    }
}
