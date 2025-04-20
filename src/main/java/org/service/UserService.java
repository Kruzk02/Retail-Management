package org.service;

import org.dto.RegisterRequest;
import org.model.User;

public interface UserService {
    User register(RegisterRequest request);
}
