package io.reactivestax.utils.database.jdbc;

import java.sql.Connection;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

public class JDBCConnectionsUtil {

    public static Connection getConnection() {

        throw new UnsupportedOperationException("Unimplemented method 'getConnection'");
    }

    public static HikariDataSource configureHikariCP(String port, String databaseName, String username,
            String password) {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl("jdbc:mysql://localhost:" + port + "/" + databaseName);
        config.setUsername(username);
        config.setPassword(password);

        // Optional HikariCP settings
        config.setMaximumPoolSize(10); // Max 10 connections in the pool
        config.setMinimumIdle(5); // Minimum idle connections
        config.setConnectionTimeout(30000); // 30 seconds timeout for obtaining a connection
        config.setIdleTimeout(600000); // 10 minutes idle timeout

        return new HikariDataSource(config);
    }
}
