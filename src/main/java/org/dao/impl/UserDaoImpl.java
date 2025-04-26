package org.dao.impl;

import lombok.AllArgsConstructor;
import org.dao.UserDao;
import org.exception.DataNotFoundException;
import org.model.Privilege;
import org.model.Role;
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
import java.util.*;

@AllArgsConstructor
public class UserDaoImpl implements UserDao {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public Boolean isUsernameOrEmailExists(String username, String email) {
        String sql = "SELECT EXISTS(SELECT 1 FROM users WHERE username = ? OR email = ?) AS user_exists";
        return jdbcTemplate.queryForObject(sql, Boolean.class, username, email);
    }

    @Override
    public User login(String username) {
        try {
            String sql = "SELECT " +
                    "u.id AS user_id, " +
                    "u.username, " +
                    "u.password, " +
                    "u.created_at, " +
                        "(SELECT STRING_AGG(DISTINCT r.name, ', ') " +
                            "FROM users_roles ur " +
                            "JOIN roles r ON ur.role_id = r.id " +
                            "WHERE ur.user_id = u.id " +
                        ") AS roles, " +
                        "(SELECT STRING_AGG(DISTINCT p.name, ',' ) " +
                            "FROM users_roles ur " +
                            "JOIN roles r ON ur.role_id = r.id " +
                            "JOIN roles_privileges rp ON r.id = rp.role_id " +
                            "JOIN privileges p ON rp.privilege_id = p.id " +
                            "WHERE ur.user_id = u.id " +
                        ") AS privileges " +
                    "FROM users u " +
                    "WHERE u.username = ? ";
            return jdbcTemplate.queryForObject(sql, new UserRowMapper(true, false, true, true, true), username);

        } catch (EmptyResultDataAccessException e) {
            throw new DataNotFoundException("User not found with a username: " + username);
        } catch (DataAccessException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED)
    @Override
    public User register(User user) {
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
            user.setId(((Number) Objects.requireNonNull(keyHolder.getKeys()).get("id")).longValue());

            String userRoleSql = "INSERT INTO users_roles(user_id, role_id) VALUES(?, ?)";
            jdbcTemplate.update(userRoleSql, ps -> {
                ps.setLong(1, user.getId());
                ps.setInt(2, 2);
            });
            return user;
        } else {
            throw new IllegalStateException("Failed to insert user into database");
        }
    }
}

@AllArgsConstructor
class UserRowMapper implements RowMapper<User> {

    private boolean includeUsername;
    private boolean includeEmail;
    private boolean includePassword;
    private boolean includeRole;
    private boolean includePrivilege;

    @Override
    public User mapRow(ResultSet rs, int rowNum) throws SQLException {
        User user = User.builder()
                .id(rs.getLong("user_id"))
                .roles(new ArrayList<>())
                .build();

        if (includeUsername) {
            user.setUsername(rs.getString("username"));
        }

        if (includeEmail) {
            user.setEmail(rs.getString("email"));
        }

        if (includePassword) {
            user.setPassword(rs.getString("password"));
        }

        if (includeRole) {
            Role role = Role.builder()
                    .name(rs.getString("roles"))
                    .privileges(new ArrayList<>())
                    .build();
            user.getRoles().add(role);

            if (includePrivilege) {
                String privilegeName = rs.getString("privileges");
                String[] arr = privilegeName.split("[,\\s]");

                for (String name : arr) {
                    Privilege privilege = Privilege.builder()
                            .name(name)
                            .build();
                    role.getPrivileges().add(privilege);
                }
            }
        }

        user.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
        return user;
    }
}