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
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

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
            ps.setInt(4, product.getQuantity());
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
                        .quantity(rs.getInt("stock_quantity"))
                        .build()
            ,id);
        } catch (EmptyResultDataAccessException e) {
            throw new DataNotFoundException("Product not found with a id: " + id);
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

        if (product.getQuantity() != null) {
            sb.append("stock_quantity = ?, " );
            params.add(product.getQuantity());
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

        return product;
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
