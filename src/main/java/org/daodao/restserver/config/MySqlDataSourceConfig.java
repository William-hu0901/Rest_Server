package org.daodao.restserver.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.sql.DataSource;

@Configuration
public class MySqlDataSourceConfig {

    @Value("${spring.datasource-mysql.url}")
    private String url;

    @Value("${spring.datasource-mysql.username}")
    private String username;

    @Value("${spring.datasource-mysql.password}")
    private String password;

    @Value("${spring.datasource-mysql.driver-class-name}")
    private String driverClassName;

    @Value("${spring.datasource-mysql.hikari.pool-name:MySQLHikariCP}")
    private String poolName;

    @Value("${spring.datasource-mysql.hikari.maximum-pool-size:10}")
    private int maximumPoolSize;

    @Value("${spring.datasource-mysql.hikari.minimum-idle:5}")
    private int minimumIdle;

    @Value("${spring.datasource-mysql.hikari.connection-timeout:5000}")
    private long connectionTimeout;

    @Value("${spring.datasource-mysql.hikari.idle-timeout:60000}")
    private long idleTimeout;

    @Value("${spring.datasource-mysql.hikari.max-lifetime:600000}")
    private long maxLifetime;

    @Value("${spring.datasource-mysql.hikari.leak-detection-threshold:60000}")
    private long leakDetectionThreshold;

    @Bean(name = "mysqlDataSource")
    @Primary
    public DataSource mysqlDataSource() {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(url);
        config.setUsername(username);
        config.setPassword(password);
        config.setDriverClassName(driverClassName);
        config.setPoolName(poolName);
        config.setMaximumPoolSize(maximumPoolSize);
        config.setMinimumIdle(minimumIdle);
        config.setConnectionTimeout(connectionTimeout);
        config.setIdleTimeout(idleTimeout);
        config.setMaxLifetime(maxLifetime);
        config.setLeakDetectionThreshold(leakDetectionThreshold);
        
        // 创建原始数据源
        HikariDataSource hikariDataSource = new HikariDataSource(config);
        
        return hikariDataSource;
    }
}
