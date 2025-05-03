package org.dao.impl;

import lombok.AllArgsConstructor;
import org.dao.CategoryDao;
import org.exception.DataNotFoundException;
import org.model.Category;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.util.Objects;

@AllArgsConstructor
@Repository
public class CategoryDaoImpl implements CategoryDao {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public Boolean isCategoryExists(Long id, String name) {
        String sql = "SELECT EXISTS(SELECT 1 FROM categories WHERE id = ? OR name = ?)";
        return jdbcTemplate.queryForObject(sql, Boolean.class, id, name);
    }

    @Override
    public Category save(Category category) {
        String sql = "INSERT INTO categories(name) VALUES(?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();

        int rowAffected = jdbcTemplate.update(conn -> {
            PreparedStatement ps = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS);
            ps.setString(1, category.getName());
            return ps;
        }, keyHolder);

        if (rowAffected > 0) {
            category.setId((Long) Objects.requireNonNull(keyHolder.getKeys()).get("id"));
            return category;
        } else {
            throw new IllegalStateException("Failed to insert category into database");
        }
    }

    @Override
    public Category findByName(String name) {
        try {
            String sql = "SELECT id, name FROM categories WHERE name = ?";
            return jdbcTemplate.queryForObject(sql,(rs, rowNum) ->
                Category.builder()
                    .id(rs.getLong("id"))
                    .name(rs.getString("name"))
                    .build()
            ,name);
        } catch (EmptyResultDataAccessException e) {
            throw new DataNotFoundException("Category not found with name: " + name);
        }
    }

    @Override
    public int deleteById(Long id) {
        String sql = "DELETE FROM categories WHERE id = ?";
        int rows = jdbcTemplate.update(sql, id);
        if (rows == 0) {
            throw new DataNotFoundException("Category not found with a id: " + id);
        }
        return rows;
    }
}
