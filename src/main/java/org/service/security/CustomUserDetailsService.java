package org.service.security;

import lombok.AllArgsConstructor;
import org.dao.EmployeeDao;
import org.model.Employee;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final EmployeeDao employeeDao;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Employee employee = employeeDao.login(username);
        if (employee == null) {
            throw new UsernameNotFoundException("User not found with a username: " + username);
        }

        return new CustomUserDetails(employee);
    }
}
