package io.reactivestax.database;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import io.reactivestax.utility.ApplicationPropertiesUtils;

import java.sql.Connection;
import java.sql.SQLException;

public class DBUtils {

    private static DBUtils instance;

    private final HikariDataSource dataSource;

    private DBUtils(ApplicationPropertiesUtils applicationPropertiesUtils) {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl("jdbc:mysql://localhost:" + applicationPropertiesUtils.getPortNumber() + "/" + applicationPropertiesUtils.getDbName());
        config.setUsername(applicationPropertiesUtils.getUsername());
        config.setPassword(applicationPropertiesUtils.getPassword());
        config.setMaximumPoolSize(20);
        dataSource = new HikariDataSource(config);
    }

    public static synchronized DBUtils getInstance(ApplicationPropertiesUtils applicationPropertiesUtils) {
        if (instance == null) {
            instance = new DBUtils(applicationPropertiesUtils);
        }
        return instance;
    }

    public Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }
}