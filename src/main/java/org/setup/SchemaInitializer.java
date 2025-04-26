package org.setup;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.init.DatabasePopulatorUtils;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;

import javax.sql.DataSource;

@AllArgsConstructor
public class SchemaInitializer implements InitializingBean {

    private final DataSource dataSource;

    @Override
    public void afterPropertiesSet() throws Exception {
        ResourceDatabasePopulator populator = new ResourceDatabasePopulator();
        populator.addScript(new ClassPathResource("sql/user.sql"));
        populator.addScript(new ClassPathResource("sql/role.sql"));
        populator.addScript(new ClassPathResource("sql/user_role.sql"));
        populator.addScript(new ClassPathResource("sql/privilege.sql"));
        populator.addScript(new ClassPathResource("sql/role_privilege.sql"));
        populator.addScript(new ClassPathResource("sql/category.sql"));
        populator.addScript(new ClassPathResource("sql/product.sql"));
        populator.addScript(new ClassPathResource("sql/insert_value.sql"));
        DatabasePopulatorUtils.execute(populator, dataSource);
    }
}
