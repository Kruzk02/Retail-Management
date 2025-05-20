package org.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.validators.RegisterRequestValidator;

@Configuration
public class ValidatorConfig {

    @Bean
    public RegisterRequestValidator registerRequestValidator() {
        return new RegisterRequestValidator();
    }
}
