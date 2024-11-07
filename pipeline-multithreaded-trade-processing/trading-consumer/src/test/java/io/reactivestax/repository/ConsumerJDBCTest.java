package io.reactivestax.repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Logger;

import org.junit.After;
import org.junit.Before;

import io.reactivestax.service.TradeService;
import io.reactivestax.util.ApplicationPropertiesUtils;
import io.reactivestax.util.database.ConnectionUtil;
import io.reactivestax.util.database.TransactionUtil;
import io.reactivestax.util.database.jdbc.JDBCTransactionUtil;
import io.reactivestax.util.factory.BeanFactory;

public class ConsumerJDBCTest {
    TradePayloadRepository tradePayloadRepository;
    ConnectionUtil<Connection> connectionUtil;
    TransactionUtil transactionUtil;
    TradeService tradeService;
    ApplicationPropertiesUtils applicationPropertiesUtils;
    Logger logger = Logger.getLogger(ConsumerJDBCTest.class.getName());

    @Before
    public void setUp() {
        applicationPropertiesUtils = ApplicationPropertiesUtils.getInstance("applicationJDBCTest.properties");
        applicationPropertiesUtils.loadApplicationProperties("applicationJDBCTest.properties");
        connectionUtil = JDBCTransactionUtil.getInstance();
        tradePayloadRepository = BeanFactory.getTradePayloadRepository();
        transactionUtil = BeanFactory.getTransactionUtil();
        tradeService = TradeService.getInstance();
        String[] sqlCommands = new String[]{
                // Drop existing tables if they exist
                "DROP TABLE IF EXISTS journal_entry",
                "DROP TABLE IF EXISTS positions",
                "DROP TABLE IF EXISTS securities_reference",
                "DROP TABLE IF EXISTS trade_payloads",

                // Create tables
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
                """,
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
                """,
                """
                CREATE TABLE securities_reference (
                    id bigint NOT NULL AUTO_INCREMENT,
                    cusip varchar(255) NOT NULL,
                    PRIMARY KEY (id),
                    UNIQUE (cusip)
                )
                """,
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
                """,
                // Insert initial data into securities_reference
                """
                INSERT INTO securities_reference (cusip)
                VALUES
                ('AAPL'),
                ('GOOGL'),
                ('AMZN'),
                ('MSFT'),
                ('TSLA'),
                ('NFLX'),
                ('FB'),
                ('NVDA'),
                ('JPM'),
                ('VISA'),
                ('MA'),
                ('BAC'),
                ('DIS'),
                ('INTC'),
                ('CSCO'),
                ('ORCL'),
                ('WMT'),
                ('T'),
                ('VZ'),
                ('ADBE'),
                ('CRM'),
                ('PYPL'),
                ('PFE'),
                ('XOM'),
                ('UNH')
                """
        };
        transactionUtil.startTransaction();
        Connection connection = connectionUtil.getConnection();
        try (Statement statement = connection.createStatement()) {
            for (String sql : sqlCommands) {
                statement.execute(sql);
            }
            transactionUtil.commitTransaction();
            logger.info("Database setup completed successfully.");
        } catch (SQLException e) {
            transactionUtil.rollbackTransaction();
            logger.warning("Error creating tables");
        }
    }

    @After
    public void cleanUp() throws SQLException {
        String dropTableSQL = "drop all objects";
        transactionUtil.startTransaction();
        Connection connection = connectionUtil.getConnection();
        try (PreparedStatement preparedStatement = connection.prepareStatement(dropTableSQL)) {
            preparedStatement.execute();
        }
        transactionUtil.commitTransaction();
        logger.info("All tables dropped successfully.");
    }
}
