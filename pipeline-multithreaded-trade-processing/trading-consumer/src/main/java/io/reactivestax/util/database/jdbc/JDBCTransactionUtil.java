package io.reactivestax.util.database.jdbc;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import io.reactivestax.type.exception.HikariCPConnectionException;
import io.reactivestax.type.exception.TransactionHandlingException;
import io.reactivestax.util.ApplicationPropertiesUtils;
import io.reactivestax.util.database.ConnectionUtil;
import io.reactivestax.util.database.TransactionUtil;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.logging.Logger;

public class JDBCTransactionUtil implements TransactionUtil, ConnectionUtil<Connection> {
    private DataSource dataSource;
    private final Logger logger = Logger.getLogger(JDBCTransactionUtil.class.getName());
    private final ThreadLocal<Connection> connectionHolder = new ThreadLocal<>();
    private static JDBCTransactionUtil instance;
    private final ApplicationPropertiesUtils applicationPropertiesUtils =
            ApplicationPropertiesUtils.getInstance();

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
            } catch (Exception e) {
                throw new HikariCPConnectionException("Error getting connection from HikariCP", e);
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
            getConnection().setAutoCommit(false);
        } catch (SQLException e) {
            logger.warning("Error while starting transaction.");
        }
    }

    private void closeConnection() {
        Connection connection = connectionHolder.get();
        if (connection != null) {
            try {
                connection.close();
            } catch (Exception e) {
                logger.warning("Error while closing the connection.");
            } finally {
                connectionHolder.remove();
            }
        }
    }

    @Override
    public void commitTransaction() {
        try {
            connectionHolder.get().commit();
            connectionHolder.get().setAutoCommit(false);
            closeConnection();
        } catch (SQLException e) {
            throw new TransactionHandlingException("error committing transaction", e);
        }
    }

    @Override
    public void rollbackTransaction() {
        try {
            connectionHolder.get().rollback();
            connectionHolder.get().setAutoCommit(false);
            closeConnection();
        } catch (SQLException e) {
            throw new TransactionHandlingException("error rolling back transaction", e);
        }
    }
}
