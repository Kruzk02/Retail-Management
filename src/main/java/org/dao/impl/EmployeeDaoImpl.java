package org.dao.impl;

import lombok.AllArgsConstructor;
import org.dao.EmployeeDao;
import org.exception.DataNotFoundException;
import org.model.Employee;
import org.model.Privilege;
import org.model.Role;
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
public class EmployeeDaoImpl implements EmployeeDao {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public Boolean isUsernameOrEmailExists(String username, String email) {
        String sql = "SELECT EXISTS(SELECT 1 FROM users WHERE username = ? OR email = ?) AS user_exists";
        return jdbcTemplate.queryForObject(sql, Boolean.class, username, email);
    }

    @Override
    public Employee login(String username) {
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
            return jdbcTemplate.queryForObject(sql, new EmployeeRowMapper(true, false, true, true, true), username);

        } catch (EmptyResultDataAccessException e) {
            throw new DataNotFoundException("User not found with a username: " + username);
        } catch (DataAccessException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED)
    @Override
    public Employee register(Employee employee) {
        String sql = "INSERT INTO users(username, email, password) VALUES(?, ? ,?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();

        int rowAffected = jdbcTemplate.update(conn -> {
            PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, employee.getUsername());
            ps.setString(2, employee.getEmail());
            ps.setString(3, employee.getPassword());
            return ps;
        }, keyHolder);

        if (rowAffected > 0) {
            employee.setId(((Number) Objects.requireNonNull(keyHolder.getKeys()).get("id")).longValue());

            String userRoleSql = "INSERT INTO users_roles(user_id, role_id) VALUES(?, ?)";
            jdbcTemplate.update(userRoleSql, ps -> {
                ps.setLong(1, employee.getId());
                ps.setInt(2, 2);
            });
            return employee;
        } else {
            throw new IllegalStateException("Failed to insert user into database");
        }
    }
}

@AllArgsConstructor
class EmployeeRowMapper implements RowMapper<Employee> {

    private boolean includeUsername;
    private boolean includeEmail;
    private boolean includePassword;
    private boolean includeRole;
    private boolean includePrivilege;

    @Override
    public Employee mapRow(ResultSet rs, int rowNum) throws SQLException {
        Employee employee = Employee.builder()
                .id(rs.getLong("user_id"))
                .roles(new ArrayList<>())
                .build();

        if (includeUsername) {
            employee.setUsername(rs.getString("username"));
        }

        if (includeEmail) {
            employee.setEmail(rs.getString("email"));
        }

        if (includePassword) {
            employee.setPassword(rs.getString("password"));
        }

        if (includeRole) {
            Role role = Role.builder()
                    .name(rs.getString("roles"))
                    .privileges(new ArrayList<>())
                    .build();
            employee.getRoles().add(role);

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

        employee.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
        return employee;
    }
}