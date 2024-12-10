//package io.reactivestax;
//
//import io.reactivestax.repository.JournalEntryRepository;
//import io.reactivestax.repository.LookupSecuritiesRepository;
//import io.reactivestax.repository.PositionsRepository;
//import io.reactivestax.repository.TradePayloadRepository;
//import io.reactivestax.service.TradeProcessorService;
//import io.reactivestax.service.TradeTestService;
//import io.reactivestax.type.dto.JournalEntry;
//import io.reactivestax.type.dto.TradePayload;
//import io.reactivestax.type.enums.LookupStatus;
//import io.reactivestax.type.enums.PostedStatus;
//import io.reactivestax.type.exception.QueryFailedException;
//import io.reactivestax.util.ApplicationPropertiesUtils;
//import io.reactivestax.util.database.ConnectionUtil;
//import io.reactivestax.util.database.TransactionUtil;
//import io.reactivestax.util.database.jdbc.JDBCTransactionUtil;
//import io.reactivestax.util.factory.BeanFactory;
//import org.junit.After;
//import org.junit.Assert;
//import org.junit.Before;
//import org.junit.Test;
//import org.mockito.Mock;
//
//import java.sql.*;
//import java.util.logging.Logger;
//
//public class ConsumerJDBCTest {
//    private TradePayloadRepository tradePayloadRepository;
//    private JournalEntryRepository journalEntryRepository;
//    private LookupSecuritiesRepository lookupSecuritiesRepository;
//    private PositionsRepository positionsRepository;
//    private ConnectionUtil<Connection> connectionUtil;
//    private TransactionUtil transactionUtil;
//    private TradeProcessorService tradeProcessorService;
//    private final TradeTestService tradeTestService = TradeTestService.getInstance();
//    private final JournalEntry journalEntryDto1 = tradeTestService.getJournalEntryDto1();
//    private final JournalEntry journalEntryDto2 = tradeTestService.getJournalEntryDto2();
//    private final Logger logger = Logger.getLogger(ConsumerJDBCTest.class.getName());
//
//
//    @Before
//    public void setUp() {
//        ApplicationPropertiesUtils applicationPropertiesUtils = ApplicationPropertiesUtils.getInstance("applicationJDBCTest.properties");
//        applicationPropertiesUtils.loadApplicationProperties("applicationJDBCTest.properties");
//        connectionUtil = JDBCTransactionUtil.getInstance();
//        tradePayloadRepository = BeanFactory.getTradePayloadRepository();
//        journalEntryRepository = BeanFactory.getJournalEntryRepository();
//        lookupSecuritiesRepository = BeanFactory.getLookupSecuritiesRepository();
//        positionsRepository = BeanFactory.getPositionsRepository();
//        tradeProcessorService = TradeProcessorService.getInstance();
//        transactionUtil = BeanFactory.getTransactionUtil();
//        String[] sqlCommands = new String[]{
//                // Drop existing tables if they exist
//                "DROP TABLE IF EXISTS journal_entry", "DROP TABLE IF EXISTS positions", "DROP TABLE IF EXISTS securities_reference", "DROP TABLE IF EXISTS trade_payloads",
//
//                // Create tables
//                """
//                CREATE TABLE journal_entry (
//                    quantity int NOT NULL,
//                    created_timestamp timestamp NOT NULL,
//                    id bigint NOT NULL AUTO_INCREMENT,
//                    transaction_timestamp timestamp NOT NULL,
//                    updated_timestamp timestamp NOT NULL,
//                    account_number varchar(255) NOT NULL,
//                    security_cusip varchar(255) NOT NULL,
//                    trade_id varchar(255) NOT NULL,
//                    direction varchar(4) NOT NULL CHECK (direction IN ('BUY', 'SELL')),
//                    posted_status varchar(10) NOT NULL CHECK (posted_status IN ('POSTED', 'NOT_POSTED')),
//                    PRIMARY KEY (id),
//                    UNIQUE (trade_id)
//                )
//                """, """
//                CREATE TABLE positions (
//                    version int NOT NULL,
//                    created_timestamp timestamp NOT NULL,
//                    holding bigint NOT NULL,
//                    updated_timestamp timestamp NOT NULL,
//                    account_number varchar(255) NOT NULL,
//                    security_cusip varchar(255) NOT NULL,
//                    PRIMARY KEY (account_number, security_cusip)
//                )
//                """, """
//                CREATE TABLE securities_reference (
//                    id bigint NOT NULL AUTO_INCREMENT,
//                    cusip varchar(255) NOT NULL,
//                    PRIMARY KEY (id),
//                    UNIQUE (cusip)
//                )
//                """, """
//                CREATE TABLE trade_payloads (
//                    created_timestamp timestamp NOT NULL,
//                    id bigint NOT NULL AUTO_INCREMENT,
//                    updated_timestamp timestamp NOT NULL,
//                    payload varchar(255) NOT NULL,
//                    trade_number varchar(255) NOT NULL,
//                    je_status varchar(10) NOT NULL CHECK (je_status IN ('POSTED', 'NOT_POSTED')),  -- Adjusted to VARCHAR with CHECK
//                    lookup_status varchar(12) NOT NULL CHECK (lookup_status IN ('PASS', 'FAIL', 'NOT_CHECKED')),
//                    validity_status varchar(7) NOT NULL CHECK (validity_status IN ('VALID', 'INVALID')),
//                    PRIMARY KEY (id),
//                    UNIQUE (trade_number)
//                )
//                """,
//                // Insert initial data into securities_reference
//                """
//                INSERT INTO securities_reference (cusip)
//                VALUES
//                ('AAPL'),
//                ('GOOGL'),
//                ('AMZN'),
//                ('MSFT'),
//                ('TSLA'),
//                ('NFLX'),
//                ('FB'),
//                ('NVDA'),
//                ('JPM'),
//                ('VISA'),
//                ('MA'),
//                ('BAC'),
//                ('DIS'),
//                ('INTC'),
//                ('CSCO'),
//                ('ORCL'),
//                ('WMT'),
//                ('T'),
//                ('VZ'),
//                ('ADBE'),
//                ('CRM'),
//                ('PYPL'),
//                ('PFE'),
//                ('XOM'),
//                ('UNH')
//                """, """
//                Insert into trade_payloads (trade_number, payload, validity_status, lookup_status, je_status, created_timestamp, updated_timestamp)
//                values
//                ('TDB_000001' ,'TDB_000001,2024-09-19 22:16:18,TDB_CUST_5214938,TSLA,BUY,1,638.02', 'VALID', 'NOT_CHECKED', 'NOT_POSTED', NOW(), NOW()),
//                ('TDB_000002' ,'TDB_000002,2024-09-19 22:16:18,TDB_CUST_5214938,V,BUY,1,638.02', 'INVALID', 'NOT_CHECKED', 'NOT_POSTED', NOW(), NOW()),
//                ('TDB_000003' ,'TDB_000003,2024-09-19 22:16:18,TDB_CUST_5214938,TSLA,BUY,1,638.02', 'VALID', 'NOT_CHECKED', 'NOT_POSTED', NOW(), NOW()),
//                ('TDB_000004' ,'TDB_000004,2024-09-19 22:16:18,TDB_CUST_5214938,TSLA,BUY,1,638.02', 'VALID', 'NOT_CHECKED', 'NOT_POSTED', NOW(), NOW()),
//                ('TDB_000005' ,'TDB_000005,2024-09-19 22:16:18,TDB_CUST_5214938,TSLA,BUY,1,638.02', 'VALID', 'NOT_CHECKED', 'NOT_POSTED', NOW(), NOW()),
//                ('TDB_000006' ,'TDB_000006,2024-09-19 22:16:18,TDB_CUST_5214938,TSLA,BUY,1,638.02', 'VALID', 'NOT_CHECKED', 'NOT_POSTED', NOW(), NOW()),
//                ('TDB_000007' ,'TDB_000007,2024-09-19 22:16:18,TDB_CUST_5214938,TSLA,BUY,1,638.02', 'VALID', 'NOT_CHECKED', 'NOT_POSTED', NOW(), NOW()),
//                ('TDB_000008' ,'TDB_000008,2024-09-19 22:16:18,TDB_CUST_5214938,TSLA,BUY,1,638.02', 'VALID', 'NOT_CHECKED', 'NOT_POSTED', NOW(), NOW()),
//                ('TDB_000009' ,'TDB_000009,2024-09-19 22:16:18,TDB_CUST_5214938,TSLA,BUY,1,638.02', 'VALID', 'NOT_CHECKED', 'NOT_POSTED', NOW(), NOW()),
//                ('TDB_000010' ,'TDB_000010,2024-09-19 22:16:18,TDB_CUST_5214938,TSLA,BUY,1,638.02', 'VALID', 'NOT_CHECKED', 'NOT_POSTED', NOW(), NOW())
//                """};
//        transactionUtil.startTransaction();
//        Connection connection = connectionUtil.getConnection();
//        try (Statement statement = connection.createStatement()) {
//            for (String sql : sqlCommands) {
//                statement.execute(sql);
//            }
//            transactionUtil.commitTransaction();
//            logger.info("Database setup completed successfully.");
//        } catch (SQLException e) {
//            transactionUtil.rollbackTransaction();
//            logger.warning("Error creating tables");
//        }
//    }
//
//    @After
//    public void cleanUp() throws SQLException {
//        String dropTableSQL = "drop all objects";
//        transactionUtil.startTransaction();
//        Connection connection = connectionUtil.getConnection();
//        try (PreparedStatement preparedStatement = connection.prepareStatement(dropTableSQL)) {
//            preparedStatement.execute();
//        }
//        transactionUtil.commitTransaction();
//        logger.info("All tables dropped successfully.");
//    }
//
//    @Test
//    public void testInsertIntoJournalEntry() {
//        Long expected = 2L;
//        journalEntryRepository.insertIntoJournalEntry(journalEntryDto1);
//        journalEntryRepository.insertIntoJournalEntry(journalEntryDto2).ifPresent(id -> Assert.assertEquals(expected, id));
//    }
//
//    @Test(expected = QueryFailedException.class)
//    public void testInsertIntoJournalEntryWithInvalidData() {
//        journalEntryRepository.insertIntoJournalEntry(journalEntryDto1);
//        journalEntryRepository.insertIntoJournalEntry(new JournalEntry());
//    }
//
//    @Test
//    public void testUpdateJournalEntryStatus() {
//        journalEntryRepository.insertIntoJournalEntry(journalEntryDto1).ifPresent(id -> {
//            String postedStatus = "";
//            journalEntryRepository.updateJournalEntryStatus(id);
//            transactionUtil.startTransaction();
//            String sql = "Select posted_status from journal_entry where id = ?";
//            Connection connection = connectionUtil.getConnection();
//            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
//                preparedStatement.setLong(1, id);
//                ResultSet resultSet = preparedStatement.executeQuery();
//                if (resultSet.next()) {
//                    postedStatus = resultSet.getString("posted_status");
//                }
//                transactionUtil.commitTransaction();
//            } catch (SQLException e) {
//                transactionUtil.rollbackTransaction();
//            }
//            Assert.assertEquals(postedStatus, PostedStatus.POSTED.name());
//        });
//    }
//
//    @Test(expected = QueryFailedException.class)
//    public void testUpdateJournalEntryStatusWithInvalidId() {
//        journalEntryRepository.updateJournalEntryStatus(3L);
//    }
//
//    @Test
//    public void testInsertPosition() {
//        int version = -1;
//        transactionUtil.startTransaction();
//        positionsRepository.upsertPosition(tradeTestService.getPositionDto1());
//        String sql = "Select version from positions where account_number = ? and security_cusip = ?";
//        Connection connection = connectionUtil.getConnection();
//        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
//            preparedStatement.setString(1, tradeTestService.getPositionDto1().getAccountNumber());
//            preparedStatement.setString(2, tradeTestService.getPositionDto1().getSecurityCusip());
//            ResultSet resultSet = preparedStatement.executeQuery();
//            if (resultSet.next()) {
//                version = resultSet.getInt("version");
//            }
//            transactionUtil.commitTransaction();
//        } catch (SQLException e) {
//            transactionUtil.rollbackTransaction();
//        }
//
//        Assert.assertEquals(0, version);
//    }
//
//    @Test
//    public void testUpsertPosition() {
//        int version = -1;
//        transactionUtil.startTransaction();
//        positionsRepository.upsertPosition(tradeTestService.getPositionDto1());
//        transactionUtil.commitTransaction();
//        transactionUtil.startTransaction();
//        positionsRepository.upsertPosition(tradeTestService.getPositionDto2());
//        transactionUtil.commitTransaction();
//        String sql = "Select version from positions where account_number = ? and security_cusip = ?";
//        Connection connection = connectionUtil.getConnection();
//        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
//            preparedStatement.setString(1, tradeTestService.getPositionDto1().getAccountNumber());
//            preparedStatement.setString(2, tradeTestService.getPositionDto1().getSecurityCusip());
//            ResultSet resultSet = preparedStatement.executeQuery();
//            if (resultSet.next()) {
//                version = resultSet.getInt("version");
//            }
//            transactionUtil.commitTransaction();
//        } catch (SQLException e) {
//            transactionUtil.rollbackTransaction();
//        }
//
//        Assert.assertEquals(1, version);
//    }
//
//    @Test
//    public void testLookupSecurities() {
//        boolean exists = lookupSecuritiesRepository.lookupSecurities("TSLA");
//        Assert.assertTrue(exists);
//    }
//
//    @Test
//    public void testLookupSecuritiesWithInvalidCusip() {
//        boolean exists = lookupSecuritiesRepository.lookupSecurities("V");
//        Assert.assertFalse(exists);
//    }
//
//    @Test
//    public void testReadRawPayload() {
//        TradePayload tradePayload = tradePayloadRepository.readRawPayload("TDB_000001");
//        Assert.assertEquals("TDB_000001,2024-09-19 22:16:18,TDB_CUST_5214938,TSLA,BUY,1,638.02", tradePayload.getPayload());
//    }
//
//    @Test
//    public void testReadRawPayloadWithInvalidId() {
//        TradePayload tradePayload = tradePayloadRepository.readRawPayload("wrong_trade_id");
//        Assert.assertNull(tradePayload.getPayload());
//    }
//
//    @Test
//    public void testUpdateTradePayloadLookupStatusPass() throws SQLException {
//        long id = 1L;
//        String lookupStatus = "";
//        tradePayloadRepository.updateTradePayloadLookupStatus(true, id);
//        String sql = "Select lookup_status from trade_payloads where id = ?";
//        Connection connection = connectionUtil.getConnection();
//        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
//            preparedStatement.setLong(1, id);
//            ResultSet resultSet = preparedStatement.executeQuery();
//            if (resultSet.next()) {
//                lookupStatus = resultSet.getString("lookup_status");
//            }
//        }
//        Assert.assertEquals(LookupStatus.PASS.name(), lookupStatus);
//    }
//
//    @Test
//    public void testUpdateTradePayloadLookupStatusFail() throws SQLException {
//        long id = 2L;
//        String lookupStatus = "";
//        tradePayloadRepository.updateTradePayloadLookupStatus(false, id);
//        String sql = "Select lookup_status from trade_payloads where id = ?";
//        Connection connection = connectionUtil.getConnection();
//        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
//            preparedStatement.setLong(1, id);
//            ResultSet resultSet = preparedStatement.executeQuery();
//            if (resultSet.next()) {
//                lookupStatus = resultSet.getString("lookup_status");
//            }
//        }
//        Assert.assertEquals(LookupStatus.FAIL.name(), lookupStatus);
//    }
//
//    @Test
//    public void testUpdateTradePayloadLookupStatusWithInvalidId() throws SQLException {
//        long id = 50L;
//        String lookupStatus = "";
//        tradePayloadRepository.updateTradePayloadLookupStatus(true, id);
//        String sql = "Select lookup_status from trade_payloads where id = ?";
//        Connection connection = connectionUtil.getConnection();
//        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
//            preparedStatement.setLong(1, id);
//            ResultSet resultSet = preparedStatement.executeQuery();
//            if (resultSet.next()) {
//                lookupStatus = resultSet.getString("lookup_status");
//            }
//        }
//        Assert.assertEquals("", lookupStatus);
//    }
//
//    @Test
//    public void testUpdateTradePayloadPostedStatus() throws SQLException {
//        long id = 1L;
//        String postedStatus = "";
//        tradePayloadRepository.updateTradePayloadPostedStatus(id);
//        String sql = "Select je_status from trade_payloads where id = ?";
//        Connection connection = connectionUtil.getConnection();
//        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
//            preparedStatement.setLong(1, id);
//            ResultSet resultSet = preparedStatement.executeQuery();
//            if (resultSet.next()) {
//                postedStatus = resultSet.getString("je_status");
//            }
//        }
//        Assert.assertEquals(PostedStatus.POSTED.name(), postedStatus);
//    }
//
//    @Test
//    public void testProcessTrade(){
//        System.out.println(journalEntryDto1.getId());
//    }
//}
