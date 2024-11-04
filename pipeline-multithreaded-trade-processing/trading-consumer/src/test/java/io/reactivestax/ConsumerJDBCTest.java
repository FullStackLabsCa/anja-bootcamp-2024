package io.reactivestax;

import io.reactivestax.repository.TradePayloadRepository;
import io.reactivestax.service.TradeService;
import io.reactivestax.util.ApplicationPropertiesUtils;
import io.reactivestax.util.database.ConnectionUtil;
import io.reactivestax.util.database.TransactionUtil;
import io.reactivestax.util.database.jdbc.JDBCTransactionUtil;
import io.reactivestax.util.factory.BeanFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Logger;

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
                """,
                """
                Insert into trade_payloads (trade_number, payload, validity_status, lookup_status, je_status, created_timestamp, updated_timestamp)
                values
                ('TDB_000001' ,'TDB_000001,2024-09-19 22:16:18,TDB_CUST_5214938,TSLA,BUY,1,638.02', 'VALID', 'NOT_CHECKED', 'NOT_POSTED', NOW(), NOW()),
                ('TDB_000002' ,'TDB_000002,2024-09-19 22:16:18,TDB_CUST_5214938,V,BUY,1,638.02', 'INVALID', 'NOT_CHECKED', 'NOT_POSTED', NOW(), NOW()),
                ('TDB_000003' ,'TDB_000003,2024-09-19 22:16:18,TDB_CUST_5214938,TSLA,BUY,1,638.02', 'VALID', 'NOT_CHECKED', 'NOT_POSTED', NOW(), NOW()),
                ('TDB_000004' ,'TDB_000004,2024-09-19 22:16:18,TDB_CUST_5214938,TSLA,BUY,1,638.02', 'VALID', 'NOT_CHECKED', 'NOT_POSTED', NOW(), NOW()),
                ('TDB_000005' ,'TDB_000005,2024-09-19 22:16:18,TDB_CUST_5214938,TSLA,BUY,1,638.02', 'VALID', 'NOT_CHECKED', 'NOT_POSTED', NOW(), NOW()),
                ('TDB_000006' ,'TDB_000006,2024-09-19 22:16:18,TDB_CUST_5214938,TSLA,BUY,1,638.02', 'VALID', 'NOT_CHECKED', 'NOT_POSTED', NOW(), NOW()),
                ('TDB_000007' ,'TDB_000007,2024-09-19 22:16:18,TDB_CUST_5214938,TSLA,BUY,1,638.02', 'VALID', 'NOT_CHECKED', 'NOT_POSTED', NOW(), NOW()),
                ('TDB_000008' ,'TDB_000008,2024-09-19 22:16:18,TDB_CUST_5214938,TSLA,BUY,1,638.02', 'VALID', 'NOT_CHECKED', 'NOT_POSTED', NOW(), NOW()),
                ('TDB_000009' ,'TDB_000009,2024-09-19 22:16:18,TDB_CUST_5214938,TSLA,BUY,1,638.02', 'VALID', 'NOT_CHECKED', 'NOT_POSTED', NOW(), NOW()),
                ('TDB_000010' ,'TDB_000010,2024-09-19 22:16:18,TDB_CUST_5214938,TSLA,BUY,1,638.02', 'VALID', 'NOT_CHECKED', 'NOT_POSTED', NOW(), NOW())
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

    @Test
    public void test(){

    }
}
