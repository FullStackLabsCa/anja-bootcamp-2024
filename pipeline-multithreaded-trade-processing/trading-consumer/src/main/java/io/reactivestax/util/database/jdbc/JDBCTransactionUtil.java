package io.reactivestax.util.database.jdbc;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import io.reactivestax.type.exception.SystemInitializationException;
import io.reactivestax.type.exception.TransactionHandlingException;
import io.reactivestax.util.ApplicationPropertiesUtils;
import io.reactivestax.util.database.ConnectionUtil;
import io.reactivestax.util.database.TransactionUtil;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.logging.Logger;

public class JDBCTransactionUtil implements TransactionUtil, ConnectionUtil<Connection> {
    private static JDBCTransactionUtil instance;
    private final Logger logger = Logger.getLogger(JDBCTransactionUtil.class.getName());
    private final ThreadLocal<Connection> connectionHolder = new ThreadLocal<>();
    private final ApplicationPropertiesUtils applicationPropertiesUtils =
            ApplicationPropertiesUtils.getInstance();
    private DataSource dataSource;

    private JDBCTransactionUtil() {
        // private constructor to prevent instantiation
    }

    public static synchronized JDBCTransactionUtil getInstance() {
        if (instance == null) {
            instance = new JDBCTransactionUtil();
        }
        return instance;
    }

    @Override
    public Connection getConnection() {
        Connection connection = connectionHolder.get();
        if (connection == null) {
            dataSource = getHikariDataSource();
            try {
                connection = dataSource.getConnection();
                connectionHolder.set(connection);
            } catch (SQLException e) {
                logger.warning("Error while getting connection.");
                throw new SystemInitializationException("Error getting connection from HikariCP");
            }
        }
        return connection;
    }

    private synchronized DataSource getHikariDataSource() {
        if (dataSource == null) {
            createDataSource();
        }
        // Configure the HikariCP connection pool
        return dataSource;
    }

    private void createDataSource() {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(applicationPropertiesUtils.getDbUrl());
        config.setUsername(applicationPropertiesUtils.getDbUsername());
        config.setPassword(applicationPropertiesUtils.getDbPassword());
        config.setMaximumPoolSize(50); // Set max connections in pool
        config.setMinimumIdle(15);
        config.setConnectionTimeout(30000); // Timeout in milliseconds
        config.setIdleTimeout(600000); // Idle timeout before connection is closed

        // Create the HikariCP data source
        dataSource = new HikariDataSource(config);
    }

    @Override
    public void startTransaction() {
        try {
            Connection connection = getConnection();
            connection.setAutoCommit(false);
        } catch (SQLException e) {
            logger.warning("Error while starting transaction.");
            throw new TransactionHandlingException("error committing transaction", e);
        }
    }

    private void closeConnection() throws SQLException {
        Connection connection = connectionHolder.get();
        if (connection != null) {
            connection.close();
            connectionHolder.remove();
        }
    }

    @Override
    public void commitTransaction() {
        try {
            Connection connection = getConnection();
            connection.commit();
            connection.setAutoCommit(true);
            closeConnection();
        } catch (SQLException e) {
            connectionHolder.remove();
            throw new TransactionHandlingException("error committing transaction", e);
        }
    }

    @Override
    public void rollbackTransaction() {
        try {
            Connection connection = getConnection();
            connection.rollback();
            connection.setAutoCommit(true);
            closeConnection();
        } catch (SQLException e) {
            connectionHolder.remove();
            throw new TransactionHandlingException("error rolling back transaction", e);
        }
    }
}
