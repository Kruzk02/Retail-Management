package org.config;


import org.dao.UserDao;
import org.service.CustomUserDetailsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;

//TODO: until web server setup success uncomment line below
//@EnableWebSecurity
@Configuration
public class SecurityConfig {

    @Bean
    public AuthenticationManager authenticationManager(UserDetailsService userDetailsService, PasswordEncoder passwordEncoder) {
        return authentication -> {
            UserDetails user = userDetailsService.loadUserByUsername(authentication.getName());

            if (passwordEncoder.matches(authentication.getCredentials().toString(), user.getPassword())) {
                return new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
            } else {
                throw new BadCredentialsException("Bad credentials");
            }
        };
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public UserDetailsService userDetailsService(UserDao userDao) {
        return new CustomUserDetailsService(userDao);
    }
}
