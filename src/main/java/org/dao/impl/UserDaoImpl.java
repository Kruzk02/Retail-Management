package org.dao.impl;

import lombok.AllArgsConstructor;
import org.dao.UserDao;
import org.exception.DataNotFoundException;
import org.model.User;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

@AllArgsConstructor
public class UserDaoImpl implements UserDao {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public User findByUsername(String username) {
        try {
            String sql = "SELECT id, email, password, created_at FROM users WHERE username = ?";
            return jdbcTemplate.queryForObject(sql, new UserRowMapper(false, true, true), username);
        } catch (EmptyResultDataAccessException e) {
            throw new DataNotFoundException("User not found with a username: " + username);
        } catch (DataAccessException e) {
            throw new RuntimeException(e.getMessage());
        }
    }
}

@AllArgsConstructor
class UserRowMapper implements RowMapper<User> {

    private boolean includeUsername;
    private boolean includeEmail;
    private boolean includePassword;

    @Override
    public User mapRow(ResultSet rs, int rowNum) throws SQLException {
        return User.builder()
                .id(rs.getLong("id"))
                .username(rs.getString("username"))
                .email(rs.getString("email"))
                .password(rs.getString("password"))
                .createdAt(rs.getTimestamp("created_at").toLocalDateTime())
                .build();
    }
}
