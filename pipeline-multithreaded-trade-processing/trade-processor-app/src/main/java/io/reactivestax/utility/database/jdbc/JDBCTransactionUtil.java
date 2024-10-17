package io.reactivestax.utility.database.jdbc;

import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.DataSource;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import io.reactivestax.exceptions.HikariCPConnectionException;
import io.reactivestax.exceptions.TransactionHandlingException;
import io.reactivestax.utility.database.ConnectionUtil;
import io.reactivestax.utility.database.TransactionUtil;

public class JDBCTransactionUtil implements TransactionUtil, ConnectionUtil<Connection> {
    private DataSource dataSource;
    private final ThreadLocal<Connection> connectionHolder = new ThreadLocal<>();
    private static JDBCTransactionUtil instance;

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
                e.printStackTrace();
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
        config.setJdbcUrl("jdbc:mysql://localhost:3306/hibernate_trade_processor");
        config.setUsername("root");
        config.setPassword("password123");
        config.setMaximumPoolSize(50); // Set max connections in pool
        config.setConnectionTimeout(30000); // Timeout in milliseconds
        config.setIdleTimeout(600000); // Idle timeout before connection is closed

        // Create the HikariCP data source
        dataSource = new HikariDataSource(config);
    }

    @Override
    public void startTransaction() {
        try {
            getConnection().setAutoCommit(false);
//            connectionHolder.get().setAutoCommit(false);
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    private void closeConnection() {
        Connection connection = connectionHolder.get();
        if (connection != null) {
            try {
                connection.close();
            } catch (Exception e) {
                e.printStackTrace();
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
            e.printStackTrace();
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
            e.printStackTrace();
            throw new TransactionHandlingException("error rolling back transaction", e);
        }
    }
}
