package org.dao.impl;

import lombok.AllArgsConstructor;
import org.dao.SupplierDao;
import org.exception.DataNotFoundException;
import org.model.Supplier;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@AllArgsConstructor
@Repository
public class SupplierDaoImpl implements SupplierDao {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public Boolean isSupplierExists(Long id) {
        String sql = "SELECT EXISTS(SELECT 1 FROM supplier WHERE id = ?)";
        return jdbcTemplate.queryForObject(sql, Boolean.class, id);
    }

    @Transactional(
            propagation = Propagation.REQUIRED,
            rollbackFor = Exception.class,
            isolation = Isolation.READ_COMMITTED
    )
    @Override
    public Supplier save(Supplier supplier) {
        String sql = "INSERT INTO suppliers(name, contact_name, phone, email, address) VALUES(?, ?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();

        int rows = jdbcTemplate.update(conn -> {
            var ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, supplier.getName());
            ps.setString(2, supplier.getContactName());
            ps.setString(3, supplier.getPhoneNumber());
            ps.setString(4, supplier.getEmail());
            ps.setString(5, supplier.getAddress());
            return ps;
        }, keyHolder);

        if (rows > 0) {
            supplier.setId(((Number) Objects.requireNonNull(keyHolder.getKeys()).get("id")).longValue());
            return supplier;
        } else {
            throw new IllegalStateException("Failed to insert supplier into database");
        }
    }

    @Override
    public List<Supplier> findAll(int limit, int offset) {
       String sql = "SELECT id, name, phone, email, address FROM suppliers limit = ? offset ?";
       return jdbcTemplate.query(sql,(rs, rowNum) ->
               Supplier.builder()
                       .id(rs.getLong("id"))
                       .name(rs.getString("name"))
                       .phoneNumber(rs.getString("phone"))
                       .email(rs.getString("email"))
                       .address(rs.getString("address"))
                       .build()
       ,limit, offset);
    }

    @Override
    public Supplier findById(Long id) {
        try {
            String sql = "SELECT name, contact_name, phone, email, address FROM suppliers WHERE id = ?";
            return jdbcTemplate.queryForObject(sql, (rs, rowNum) ->
               Supplier.builder()
                       .name(rs.getString("name"))
                       .contactName(rs.getString("contact_name"))
                       .phoneNumber(rs.getString("phone"))
                       .email(rs.getString("email"))
                       .address(rs.getString("address"))
                       .build()
            , id);
        } catch (EmptyResultDataAccessException e) {
            throw new DataNotFoundException("Supplier not found with a id: " + id);
        }
    }

    @Transactional
    @Override
    public Supplier update(Long id, Supplier supplier) {
        StringBuilder sb = new StringBuilder("UPDATE suppliers SET");
        List<Object> params = new ArrayList<>();

        if (supplier.getName() != null) {
            sb.append("name = ?, ");
            params.add(supplier.getName());
        }

        if (supplier.getContactName() != null) {
            sb.append("contact_name = ?, ");
            params.add(supplier.getContactName());
        }

        if (supplier.getEmail() != null) {
            sb.append("email = ?, ");
            params.add(supplier.getEmail());
        }

        if (supplier.getAddress() != null) {
            sb.append("address = ?, ");
            params.add(supplier.getAddress());
        }

        if (supplier.getPhoneNumber() != null) {
            sb.append("phone = ?, ");
            params.add(supplier.getPhoneNumber());
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
        int rows = jdbcTemplate.update(sql, params.toArray());

        return rows > 0 ? supplier : null;
    }

    @Override
    public int deleteById(Long id) {
       String sql = "DELETE FROM suppliers WHERE id = ?";
       int rows = jdbcTemplate.update(sql, id);
       if (rows == 0) {
           throw new DataNotFoundException("Supplier not found with a id: " + id);
       }
       return rows;
    }
}
