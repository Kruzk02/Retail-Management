package org.dao.impl;

import lombok.AllArgsConstructor;
import org.dao.LocationDao;
import org.exception.DataNotFoundException;
import org.model.Location;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.Objects;

@AllArgsConstructor
@Repository
public class LocationDaoImpl implements LocationDao {

    private JdbcTemplate jdbcTemplate;

    @Override
    public Boolean isLocationExist(Long id, String name) {
        String sql = "SELECT EXISTS(SELECT 1 FROM locations WHERE id = ? or name = ?)";
        return jdbcTemplate.queryForObject(sql, Boolean.class, id, name);
    }

    @Override
    public Location save(Location location) {
        String sql = "INSERT INTO locations(name) VALUES(?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();

        int rows = jdbcTemplate.update(conn -> {
            PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, location.getName());
            return ps;
        }, keyHolder);

        if (rows > 0) {
            location.setId(((Number) Objects.requireNonNull(keyHolder.getKeys()).get("id")).longValue());
            return location;
        } else {
            throw new IllegalStateException("Failed to insert locations into database");
        }
    }

    @Override
    public Location findById(Long id) {
        try {
            String sql = "SELECT id, name FROM locations WHERE id = ?";
            return jdbcTemplate.queryForObject(sql, (rs, rowNUm) ->
                            Location.builder()
                                    .id(rs.getLong("id"))
                                    .name(rs.getString("name"))
                                    .build()
                    , id);
        } catch (EmptyResultDataAccessException e) {
            throw new DataNotFoundException("Location not found with id: " + id);
        }
    }

    @Override
    public Location findByName(String name) {
        try {
            String sql = "SELECT id, name FROM locations WHERE name = ?";
            return jdbcTemplate.queryForObject(sql, (rs, rowNUm) ->
                Location.builder()
                        .id(rs.getLong("id"))
                        .name(rs.getString("name"))
                        .build()
            , name);
        } catch (EmptyResultDataAccessException e) {
            throw new DataNotFoundException("Location not found with name: " + name);
        }
    }

    @Override
    public int deleteById(Long id) {
        String sql = "DELETE FROM locations WHERE id = ?";
        int rows = jdbcTemplate.update(sql, id);
        if (rows == 0) {
            throw new DataNotFoundException("Location not found with a id: " + id);
        }
        return rows;
    }
}
