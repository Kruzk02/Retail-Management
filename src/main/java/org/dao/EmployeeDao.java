package org.dao;

import org.model.Employee;

public interface UserDao {
    //TODO: add more after setup security
    Boolean isUsernameOrEmailExists(String username, String email);
    Employee login(String username);
    Employee register(Employee employee);
}
