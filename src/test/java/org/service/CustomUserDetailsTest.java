package org.service;

import org.junit.jupiter.api.Test;
import org.model.User;

import static org.junit.jupiter.api.Assertions.*;

class CustomUserDetailsTest {

    @Test
    void testUsernameAndPassword() {
        var user = User.builder().username("testuser").password("testpass").build();
        var details = new CustomUserDetails(user);

        assertEquals("testuser", details.getUsername());
        assertEquals("testpass", details.getPassword());
        assertTrue(details.getAuthorities().isEmpty());
    }
}