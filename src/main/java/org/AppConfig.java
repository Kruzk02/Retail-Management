package org;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import({ ServiceConfig.class, DatabaseConfig.class })
public class AppConfig {
}
