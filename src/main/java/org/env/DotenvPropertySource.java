package org.env;

import org.springframework.core.env.PropertySource;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class DotenvPropertySource extends PropertySource<Properties> {

    public DotenvPropertySource(String name) throws IOException {
        super(name, new Properties());

        try (FileInputStream inputStream = new FileInputStream(".env")) {
            this.source.load(inputStream);
        }
    }

    @Override
    public Object getProperty(String name) {
        return this.source.getProperty(name);
    }
}
