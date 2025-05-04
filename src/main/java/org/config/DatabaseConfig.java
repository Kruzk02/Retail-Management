package org.config;

import com.zaxxer.hikari.HikariDataSource;
import org.setup.SchemaInitializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;

@Configuration
@PropertySource("classpath:app.properties")
public class DatabaseConfig {

    @Value("${db.url}")
    private String url;
    @Value("${db.user}")
    private String username;
    @Value("${db.password}")
    private String password;

    @Bean public DataSource dataSource() {
        var dataSource = new HikariDataSource();

        dataSource.setDriverClassName("org.postgresql.Driver");
        dataSource.setJdbcUrl(url);
        dataSource.setUsername(username);
        dataSource.setPassword(password);

        dataSource.setMaximumPoolSize(12);
        dataSource.setMinimumIdle(4);

        dataSource.setIdleTimeout(30000);
        dataSource.setMaxLifetime(600000);
        dataSource.setConnectionTimeout(30000);

        dataSource.setConnectionTestQuery("SELECT 1");
        return dataSource;
    }

    @Bean public JdbcTemplate jdbcTemplate(DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }

    @Bean public SchemaInitializer schemaInitializer(DataSource dataSource) {
        return new SchemaInitializer(dataSource);
    }
}
