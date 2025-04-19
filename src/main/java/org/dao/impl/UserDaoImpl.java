package org.dao.impl;

import lombok.AllArgsConstructor;
import org.dao.UserDao;
import org.exception.DataNotFoundException;
import org.model.User;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Objects;

@AllArgsConstructor
public class UserDaoImpl implements UserDao {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public Boolean isUsernameOrEmailExists(String username, String email) {
        String sql = "SELECT EXISTS(SELECT 1 FROM users WHERE username = ? OR email = ?) AS user_exists";
        return jdbcTemplate.queryForObject(sql, Boolean.class, username, email);
    }

    @Override
    public User findPasswordByUsername(String username) {
        try {
            String sql = "SELECT password, created_at FROM users WHERE username = ?";
            return jdbcTemplate.queryForObject(sql, new UserRowMapper(false, true, true), username);
        } catch (EmptyResultDataAccessException e) {
            throw new DataNotFoundException("User not found with a username: " + username);
        } catch (DataAccessException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED)
    @Override
    public User register(User user) {
        try {
            String sql = "INSERT INTO users(username, email, password) VALUES(?, ? ,?)";
            KeyHolder keyHolder = new GeneratedKeyHolder();

            int rowAffected = jdbcTemplate.update(conn -> {
                PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                ps.setString(1, user.getUsername());
                ps.setString(2, user.getEmail());
                ps.setString(3, user.getPassword());
                return ps;
            }, keyHolder);

            if (rowAffected > 0) {
                user.setId(Objects.requireNonNull(keyHolder.getKey()).longValue());
                return user;
            } else {
                throw new IllegalStateException("Failed to insert user into database");
            }
        } catch (DataAccessException e) {
            throw new RuntimeException("Database error during user registration", e);
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
