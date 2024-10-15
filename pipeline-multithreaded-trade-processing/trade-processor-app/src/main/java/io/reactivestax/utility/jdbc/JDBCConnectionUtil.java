package io.reactivestax.utility.jdbc;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import io.reactivestax.utility.ApplicationPropertiesUtils;

import java.sql.Connection;
import java.sql.SQLException;

public class JDBCConnectionUtil {

    private static JDBCConnectionUtil instance;

    private final HikariDataSource dataSource;

    private JDBCConnectionUtil(ApplicationPropertiesUtils applicationPropertiesUtils) {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl("jdbc:mysql://localhost:" + applicationPropertiesUtils.getPortNumber() + "/" + applicationPropertiesUtils.getDbName());
        config.setUsername(applicationPropertiesUtils.getUsername());
        config.setPassword(applicationPropertiesUtils.getPassword());
        config.setMaximumPoolSize(20);
        dataSource = new HikariDataSource(config);
    }

    public static synchronized JDBCConnectionUtil getInstance(ApplicationPropertiesUtils applicationPropertiesUtils) {
        if (instance == null) {
            instance = new JDBCConnectionUtil(applicationPropertiesUtils);
        }
        return instance;
    }

    public Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }
}