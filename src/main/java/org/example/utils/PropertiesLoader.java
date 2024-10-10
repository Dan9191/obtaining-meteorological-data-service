package org.example.utils;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class PropertiesLoader {

    private static final String RESOURCE_FILE_PATH = "src/main/resources/application.properties";
    public static Properties loadProperties() throws IOException {

        Properties appProps = new Properties();
        appProps.load(new FileInputStream(RESOURCE_FILE_PATH));
        return appProps;
    }
}
