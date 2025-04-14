package org.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import({ ServiceConfig.class, DatabaseConfig.class, SecurityConfig.class })
public class AppConfig {
}
