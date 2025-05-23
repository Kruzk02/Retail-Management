package org.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.validators.*;

@Configuration
public class ValidatorConfig {

    @Bean
    public RegisterRequestValidator registerRequestValidator() {
        return new RegisterRequestValidator();
    }

    @Bean
    public LoginRequestValidator loginRequestValidator() {
        return new LoginRequestValidator();
    }

    @Bean
    public ProductRequestValidator productRequestValidator() {
        return new ProductRequestValidator();
    }

    @Bean
    public SupplierRequestValidator supplierRequestValidator() {
        return new SupplierRequestValidator();
    }

    @Bean
    public InventoryRequestValidator inventoryRequestValidator() {
        return new InventoryRequestValidator();
    }
}
