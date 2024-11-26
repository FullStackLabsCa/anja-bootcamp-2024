package io.reactivestax.repository.jdbc;

import io.reactivestax.repository.TradePayloadRepository;
import io.reactivestax.type.dto.TradePayload;
import io.reactivestax.util.ApplicationPropertiesUtils;
import io.reactivestax.util.database.ConnectionUtil;
import io.reactivestax.util.database.TransactionUtil;
import io.reactivestax.util.database.jdbc.JDBCTransactionUtil;
import io.reactivestax.util.factory.BeanFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.sql.*;
import java.util.logging.Logger;

import static org.junit.Assert.assertEquals;

public class JDBCTradePayloadRepositoryTest {
    TradePayloadRepository tradePayloadRepository;
    ConnectionUtil<Connection> connectionUtil;
    TransactionUtil transactionUtil;
    ApplicationPropertiesUtils applicationPropertiesUtils;
    Logger logger = Logger.getLogger(JDBCTradePayloadRepositoryTest.class.getName());

    @Before
    public void setUp() {
        applicationPropertiesUtils = ApplicationPropertiesUtils.getInstance("applicationJDBCTest.properties");
        applicationPropertiesUtils.loadApplicationProperties("applicationJDBCTest.properties");
        connectionUtil = JDBCTransactionUtil.getInstance();
        tradePayloadRepository = BeanFactory.getTradePayloadRepository();
        transactionUtil = BeanFactory.getTransactionUtil();
        String[] sqlCommands = new String[]{
                // Drop existing tables if they exist
                "DROP TABLE IF EXISTS trade_payloads",

                // Create table
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
    public void testInsertRawPayloadWithTwoDifferentRecords() throws SQLException {
        int count = 0;
        transactionUtil.startTransaction();
        TradePayload tradePayload1 = new TradePayload();
        tradePayload1.setPayload("TDB_00000001,2024-09-25 06:58:37,TDB_CUST_2517563,TSLA,SELL,45,1480.82");
        tradePayload1.setTradeNumber("TDB_00000001");
        tradePayload1.setValidityStatus("VALID");
        transactionUtil.commitTransaction();
        tradePayloadRepository.insertTradeRawPayload(tradePayload1);
        TradePayload tradePayload2 = new TradePayload();
        tradePayload2.setPayload("TDB_00000002,2024-09-25 06:58:37,TDB_CUST_2517563,V,SELL,45,1480.82");
        tradePayload2.setTradeNumber("TDB_00000002");
        tradePayload2.setValidityStatus("INVALID");
        tradePayloadRepository.insertTradeRawPayload(tradePayload2);
        Connection connection = connectionUtil.getConnection();
        try (PreparedStatement preparedStatement = connection.prepareStatement(" Select count(*) as count from trade_payloads")) {
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                count = resultSet.getInt("count");
                System.out.println(count);
            }
        }
        assertEquals(2, count);
    }

    @Test
    public void testInsertRawPayloadWithTwoSameRecords() throws SQLException {
        int count = 0;
        TradePayload tradePayload1 = new TradePayload();
        tradePayload1.setPayload("TDB_00000001,2024-09-25 06:58:37,TDB_CUST_2517563,TSLA,SELL,45,1480.82");
        tradePayload1.setTradeNumber("TDB_00000001");
        tradePayload1.setValidityStatus("VALID");
        transactionUtil.startTransaction();
        tradePayloadRepository.insertTradeRawPayload(tradePayload1);
        transactionUtil.commitTransaction();
        TradePayload tradePayload2 = new TradePayload();
        tradePayload2.setPayload("TDB_00000001,2024-09-25 06:58:37,TDB_CUST_2517563,TSLA,SELL,45,1480.82");
        tradePayload2.setTradeNumber("TDB_00000001");
        tradePayload2.setValidityStatus("VALID");
        transactionUtil.startTransaction();
        tradePayloadRepository.insertTradeRawPayload(tradePayload2);
        transactionUtil.commitTransaction();
        transactionUtil.startTransaction();
        Connection connection = connectionUtil.getConnection();
        try (PreparedStatement preparedStatement = connection.prepareStatement("Select count(*) as count from " + "trade_payloads")) {
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                count = resultSet.getInt("count");
                System.out.println(count);
            }
            transactionUtil.commitTransaction();
        }
        assertEquals(1, count);
    }

}
