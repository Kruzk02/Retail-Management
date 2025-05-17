package org.service;

import org.junit.jupiter.api.Test;
import org.model.Role;
import org.model.Employee;
import org.service.security.CustomUserDetails;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CustomUserDetailsTest {

    @Test
    void testUsernameAndPassword() {
        var user = Employee.builder()
                .username("testuser")
                .password("testpass")
                .roles(List.of(Role.builder()
                        .id(1L)
                        .name("USER")
                        .privileges(Collections.emptySet())
                        .build())
                ).build();
        var details = new CustomUserDetails(user);

        var expectedAuthority = new SimpleGrantedAuthority("USER");
        assertEquals("testuser", details.getUsername());
        assertEquals("testpass", details.getPassword());
        assertTrue(details.getAuthorities().contains(expectedAuthority));
        assertFalse(details.getAuthorities().isEmpty());
    }
}