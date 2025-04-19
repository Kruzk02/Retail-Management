package org.dao;

import org.model.User;

public interface UserDao {
    //TODO: add more after setup security
    Boolean isUsernameOrEmailExists(String username, String email);
    User findPasswordByUsername(String username);
    User register(User user);
}
