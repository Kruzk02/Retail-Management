package org.dao.impl;

import lombok.AllArgsConstructor;
import org.dao.InventoryDao;
import org.exception.DataNotFoundException;
import org.model.Inventory;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@AllArgsConstructor
@Repository
public class InventoryDaoImpl implements InventoryDao {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public Boolean isInventoryExist(Long id) {
        String sql = "SELECT EXISTS(SELECT 1 FROM inventory WHERE id = ?)";
        return jdbcTemplate.queryForObject(sql, Boolean.class, id);
    }

    @Transactional(
            propagation = Propagation.REQUIRED,
            rollbackFor = Exception.class,
            isolation = Isolation.READ_COMMITTED
    )
    @Override
    public Inventory save(Inventory inventory) {
        String sql = "INSERT INTO inventory(product_id, location_id, quantity) VALUES(?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();

        int rows = jdbcTemplate.update(conn -> {
            PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setLong(1, inventory.getProduct().getId());
            ps.setLong(2, inventory.getLocation().getId());
            ps.setInt(3, inventory.getQuantity());
            return ps;
        }, keyHolder);

        if (rows > 0) {
            inventory.setId((Long) Objects.requireNonNull(keyHolder.getKeys()).get("id"));
            return inventory;
        } else {
            throw new IllegalStateException("Failed to insert inventory to database");
        }
    }

    @Override
    public Inventory findById(Long id) {
        try {
            String sql = "SELECT id, quantity, updated_at FROM inventory WHERE id = ?";
            return jdbcTemplate.queryForObject(sql, (rs, rowNum) ->
                Inventory.builder()
                        .id(rs.getLong("id"))
                        .quantity(rs.getInt("quantity"))
                        .updatedAt(rs.getTimestamp("updated_at").toLocalDateTime())
                        .build()
            , id);
        } catch (EmptyResultDataAccessException e) {
            throw new DataNotFoundException("Inventory not found with a id: " + id);
        }
    }

    @Transactional
    @Override
    public Inventory update(Long id, Inventory inventory) {
        StringBuilder sb = new StringBuilder("UPDATE inventory SET ");
        List<Object> params = new ArrayList<>();

        if (inventory.getProduct().getId() > 0) {
            sb.append("product_id = ?, ");
            params.add(inventory.getProduct().getId());
        }

        if (inventory.getLocation().getId() > 0) {
            sb.append("location_id = ?, ");
            params.add(inventory.getLocation().getId());
        }

        if (inventory.getQuantity() > 0) {
            sb.append("quantity = ?, ");
            params.add(inventory.getQuantity());
        }

        if (params.isEmpty()) {
            throw new IllegalArgumentException("No field to update");
        }

        if (!sb.isEmpty()) {
            sb.setLength(sb.length() - 2);
        }

        sb.append("updated_at = ?, ");
        params.add(Timestamp.from(Instant.now()));
        sb.append(" WHERE id = ?");
        params.add(id);

        String sql = sb.toString();
        int rows = jdbcTemplate.update(sql, params.toArray());

        return rows > 0 ? inventory : null;
    }

    @Transactional
    @Override
    public int deleteById(Long id) {
        String sql = "DELETE FROM inventory WHERE id = ?";
        int rows = jdbcTemplate.update(sql, id);
        if (rows == 0) {
            throw new DataNotFoundException("Inventory not found with a id: " + id);
        }
        return rows;
    }
}
