package io.reactivestax.util;

import io.reactivestax.util.database.ConnectionUtil;
import io.reactivestax.util.database.jdbc.JDBCTransactionUtil;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Logger;

public class DbSetUpUtil {
    private final Logger logger = Logger.getLogger(DbSetUpUtil.class.getName());

    public void createJournalEntryTable() throws SQLException {
        ApplicationPropertiesUtils applicationPropertiesUtils = ApplicationPropertiesUtils.getInstance("applicationJDBCTest.properties");
        applicationPropertiesUtils.loadApplicationProperties("applicationJDBCTest.properties");
        String[] sqlCommands = new String[]{
                "DROP TABLE IF EXISTS journal_entry",
                """
                CREATE TABLE journal_entry (
                    quantity int NOT NULL,
                    created_timestamp timestamp NOT NULL,
                    id bigint NOT NULL AUTO_INCREMENT,
                    transaction_timestamp timestamp NOT NULL,
                    updated_timestamp timestamp NOT NULL,
                    account_number varchar(255) NOT NULL,
                    security_cusip varchar(255) NOT NULL,
                    trade_id varchar(255) NOT NULL,
                    direction varchar(4) NOT NULL CHECK (direction IN ('BUY', 'SELL')),
                    posted_status varchar(10) NOT NULL CHECK (posted_status IN ('POSTED', 'NOT_POSTED')),
                    PRIMARY KEY (id),
                    UNIQUE (trade_id)
                )
                """};
        ConnectionUtil<Connection> connectionUtil = JDBCTransactionUtil.getInstance();
        Connection connection = connectionUtil.getConnection();
        try (Statement statement = connection.createStatement()) {
            for (String sql : sqlCommands) {
                statement.execute(sql);
            }
            logger.info("Database setup completed successfully.");
        }
    }

    public void createPositionsTable() throws SQLException {
        ApplicationPropertiesUtils applicationPropertiesUtils = ApplicationPropertiesUtils.getInstance("applicationJDBCTest.properties");
        applicationPropertiesUtils.loadApplicationProperties("applicationJDBCTest.properties");
        String[] sqlCommands = new String[]{
                "DROP TABLE IF EXISTS positions",
                """
                CREATE TABLE positions (
                    version int NOT NULL,
                    created_timestamp timestamp NOT NULL,
                    holding bigint NOT NULL,
                    updated_timestamp timestamp NOT NULL,
                    account_number varchar(255) NOT NULL,
                    security_cusip varchar(255) NOT NULL,
                    PRIMARY KEY (account_number, security_cusip)
                )
                """};
        ConnectionUtil<Connection> connectionUtil = JDBCTransactionUtil.getInstance();
        Connection connection = connectionUtil.getConnection();
        try (Statement statement = connection.createStatement()) {
            for (String sql : sqlCommands) {
                statement.execute(sql);
            }
            logger.info("Database setup completed successfully.");
        }
    }

    public void createSecuritiesReferenceTable() throws SQLException {
        ApplicationPropertiesUtils applicationPropertiesUtils = ApplicationPropertiesUtils.getInstance("applicationJDBCTest.properties");
        applicationPropertiesUtils.loadApplicationProperties("applicationJDBCTest.properties");
        String[] sqlCommands = new String[]{
                "DROP TABLE IF EXISTS securities_reference",
                """
                CREATE TABLE securities_reference (
                    id bigint NOT NULL AUTO_INCREMENT,
                    cusip varchar(255) NOT NULL,
                    PRIMARY KEY (id),
                    UNIQUE (cusip)
                )
                """,
                """
                INSERT INTO securities_reference (cusip) VALUES ('AAPL'), ('GOOGL')
                """};
        ConnectionUtil<Connection> connectionUtil = JDBCTransactionUtil.getInstance();
        Connection connection = connectionUtil.getConnection();
        try (Statement statement = connection.createStatement()) {
            for (String sql : sqlCommands) {
                statement.execute(sql);
            }
            logger.info("Database setup completed successfully.");
        }
    }

    public void createTradePayloadsTable() throws SQLException {
        ApplicationPropertiesUtils applicationPropertiesUtils = ApplicationPropertiesUtils.getInstance("applicationJDBCTest.properties");
        applicationPropertiesUtils.loadApplicationProperties("applicationJDBCTest.properties");
        String[] sqlCommands = new String[]{
                "DROP TABLE IF EXISTS trade_payloads",
                """
                CREATE TABLE trade_payloads (
                    created_timestamp timestamp NOT NULL,
                    id bigint NOT NULL AUTO_INCREMENT,
                    updated_timestamp timestamp NOT NULL,
                    payload varchar(255) NOT NULL,
                    trade_number varchar(255) NOT NULL,
                    je_status varchar(10) NOT NULL CHECK (je_status IN ('POSTED', 'NOT_POSTED')),  -- Adjusted to VARCHAR with CHECK
                    lookup_status varchar(12) NOT NULL CHECK (lookup_status IN ('PASS', 'FAIL', 'NOT_CHECKED')),
                    validity_status varchar(7) NOT NULL CHECK (validity_status IN ('VALID', 'INVALID')),
                    PRIMARY KEY (id),
                    UNIQUE (trade_number)
                )
                """};
        ConnectionUtil<Connection> connectionUtil = JDBCTransactionUtil.getInstance();
        Connection connection = connectionUtil.getConnection();
        try (Statement statement = connection.createStatement()) {
            for (String sql : sqlCommands) {
                statement.execute(sql);
            }
            logger.info("Database setup completed successfully.");
        }
    }
}
