package com.contact.management.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import com.contact.management.component.jdbc.MyJdbcTemplate;
import com.contact.management.component.jdbc.connection.ConnectionProvider;
import com.contact.management.component.jdbc.connection.PGConnectionProvider;
import com.zaxxer.hikari.HikariDataSource;

@Configuration
public class DatabaseConfig {

    @Value("${spring.datasource.driver-class-name}")
    private String primaryDriver;
    @Value("${spring.datasource.url}")
    private String primaryUrl;
    @Value("${spring.datasource.username}")
    private String primaryUsername;
    @Value("${spring.datasource.password}")
    private String primaryPassword;

    @Value("${spring.second-datasource.driver-class-name}")
    private String secondaryDriver;
    @Value("${spring.second-datasource.url}")
    private String secondaryUrl;
    @Value("${spring.second-datasource.username}")
    private String secondaryUsername;
    @Value("${spring.second-datasource.password}")
    private String secondaryPassword;

    @Bean(name = "dataSourceOne")
    @Primary
    public HikariDataSource primaryDataSource() {
        return DataSourceBuilder.create()
                .type(HikariDataSource.class)
                .driverClassName(primaryDriver)
                .url(primaryUrl)
                .username(primaryUsername)
                .password(primaryPassword)
                .build();
    }

    @Bean(name = "dataSourceTwo")
    public HikariDataSource secondaryDataSource() {
        return DataSourceBuilder.create()
                .type(HikariDataSource.class)
                .driverClassName(secondaryDriver)
                .url(secondaryUrl)
                .username(secondaryUsername)
                .password(secondaryPassword)
                .build();
    }

    @Bean
    public ConnectionProvider connectionProvider(
            @Qualifier("dataSourceOne") HikariDataSource primary,
            @Qualifier("dataSourceTwo") HikariDataSource secondary) {
        return new PGConnectionProvider(primary, secondary);
    }

    @Bean
    public MyJdbcTemplate myJdbcTemplate(ConnectionProvider connectionProvider) {
        return new MyJdbcTemplate(connectionProvider);
    }
}
