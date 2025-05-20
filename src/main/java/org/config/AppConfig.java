package org.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import({ DaoConfig.class, ServiceConfig.class, DatabaseConfig.class, SecurityConfig.class, ValidatorConfig.class })
public class AppConfig { }
