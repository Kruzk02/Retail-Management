package org.dao;

import org.model.User;

public interface UserDao {
    //TODO: add more after setup security
    User findByUsername(String username);
    User register(User user);
}
