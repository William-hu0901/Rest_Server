package org.daodao.restserver.dbconfig;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.daodao.restserver.exceptions.PropertyException;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

@Slf4j
@Getter
public class DatabaseConfig {
    private static final String PROPERTIES_FILE = "application.properties";
    
    private final String postgresHost;
    private final int postgresPort;
    private final String postgresDatabase;
    private final String postgresUsername;
    private final String postgresPassword;
    private final String postgresSql;

    public DatabaseConfig() {
        Properties properties = loadProperties();
        this.postgresHost = getProperty(properties, "postgres.host");
        this.postgresPort = Integer.parseInt(getProperty(properties, "postgres.port"));
        this.postgresDatabase = getProperty(properties, "postgres.database");
        this.postgresUsername = getProperty(properties, "postgres.username");
        this.postgresPassword = getProperty(properties, "postgres.password");
        this.postgresSql = getProperty(properties, "postgres.sql");
        
        log.info("Database configuration loaded successfully");
    }
    
    private Properties loadProperties() {
        Properties properties = new Properties();
        try (InputStream input = getClass().getClassLoader().getResourceAsStream(PROPERTIES_FILE)) {
            if (input == null) {
                throw new PropertyException("Unable to find " + PROPERTIES_FILE);
            }
            properties.load(input);
        } catch (IOException e) {
            throw new PropertyException("Error loading properties file: " + PROPERTIES_FILE, e);
        }
        return properties;
    }
    
    private String getProperty(Properties properties, String key) {
        String value = properties.getProperty(key);
        if (value == null || value.trim().isEmpty()) {
            log.error("Required property '" + key + "' is missing or empty in " + PROPERTIES_FILE);
            return "";
        }
        return value.trim();
    }
}