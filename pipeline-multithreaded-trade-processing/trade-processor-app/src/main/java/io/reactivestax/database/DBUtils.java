package io.reactivestax.database;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import io.reactivestax.utility.ApplicationPropertiesUtils;

import java.sql.Connection;
import java.sql.SQLException;

public class DBUtils {

    private static DBUtils instance;

    private final HikariDataSource dataSource;

    private DBUtils() {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl("jdbc:mysql://localhost:" + ApplicationPropertiesUtils.getPortNumber() + "/" + ApplicationPropertiesUtils.getDbName());
        config.setUsername(ApplicationPropertiesUtils.getUsername());
        config.setPassword(ApplicationPropertiesUtils.getPassword());
        config.setMaximumPoolSize(20);
        dataSource = new HikariDataSource(config);
    }

    public static synchronized DBUtils getInstance() {
        if (instance == null) {
            instance = new DBUtils();
        }
        return instance;
    }

    public Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }
}