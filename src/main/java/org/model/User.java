package org.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Collection;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class User {

    private Long id;
    private String username;
    private String email;
    private String password;
    private Collection<Role> roles;
    private LocalDateTime createdAt;

}
