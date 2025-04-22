package org.dto;

public record LoginRequest(String username, String password) {

    public LoginRequest {
        if (username == null || username.isBlank()) {
            throw new IllegalArgumentException("Username cannot be blank");
        }

        if (password == null || password.isBlank()) {
            throw new IllegalArgumentException("Password cannot be blank");
        }
    }
}
