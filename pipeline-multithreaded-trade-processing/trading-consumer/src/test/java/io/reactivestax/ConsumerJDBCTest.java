package io.reactivestax;

import io.reactivestax.repository.JournalEntryRepository;
import io.reactivestax.repository.LookupSecuritiesRepository;
import io.reactivestax.repository.PositionsRepository;
import io.reactivestax.repository.TradePayloadRepository;
import io.reactivestax.service.TradeService;
import io.reactivestax.service.TradeTestService;
import io.reactivestax.type.dto.JournalEntry;
import io.reactivestax.type.dto.Position;
import io.reactivestax.type.enums.PostedStatus;
import io.reactivestax.type.exception.OptimisticLockingException;
import io.reactivestax.type.exception.QueryFailedException;
import io.reactivestax.util.ApplicationPropertiesUtils;
import io.reactivestax.util.database.ConnectionUtil;
import io.reactivestax.util.database.TransactionUtil;
import io.reactivestax.util.database.jdbc.JDBCTransactionUtil;
import io.reactivestax.util.factory.BeanFactory;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.sql.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicReference;
import java.util.logging.Logger;

public class ConsumerJDBCTest {
    private TradePayloadRepository tradePayloadRepository;
    private JournalEntryRepository journalEntryRepository;
    private LookupSecuritiesRepository lookupSecuritiesRepository;
    private PositionsRepository positionsRepository;
    private ConnectionUtil<Connection> connectionUtil;
    private TransactionUtil transactionUtil;
    private TradeService tradeService;
    private ApplicationPropertiesUtils applicationPropertiesUtils;
    private final TradeTestService tradeTestService = TradeTestService.getInstance();
    private final JournalEntry journalEntryDto1 = tradeTestService.getJournalEntryDto1();
    private final JournalEntry journalEntryDto2 =
            tradeTestService.getJournalEntryDto2();
    private final Logger logger = Logger.getLogger(ConsumerJDBCTest.class.getName());
    private final int threadCount = 30;
    private final ExecutorService executorService = Executors.newFixedThreadPool(threadCount);

    @Before
    public void setUp() {
        applicationPropertiesUtils = ApplicationPropertiesUtils.getInstance("applicationJDBCTest.properties");
        applicationPropertiesUtils.loadApplicationProperties("applicationJDBCTest.properties");
        connectionUtil = JDBCTransactionUtil.getInstance();
        tradePayloadRepository = BeanFactory.getTradePayloadRepository();
        journalEntryRepository = BeanFactory.getJournalEntryRepository();
        lookupSecuritiesRepository = BeanFactory.getLookupSecuritiesRepository();
        positionsRepository = BeanFactory.getPositionsRepository();
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
    public void testInsertIntoJournalEntry() {
        Long expected = 2L;
        journalEntryRepository.insertIntoJournalEntry(journalEntryDto1);
        Long id2 = journalEntryRepository.insertIntoJournalEntry(journalEntryDto2);
        Assert.assertEquals(expected, id2);
    }

    @Test(expected = QueryFailedException.class)
    public void testInsertIntoJournalEntryWithInvalidData() {
        journalEntryRepository.insertIntoJournalEntry(journalEntryDto1);
        journalEntryRepository.insertIntoJournalEntry(new JournalEntry());
    }

    @Test
    public void testUpdateJournalEntryStatus() {
        Long id = journalEntryRepository.insertIntoJournalEntry(journalEntryDto1);
        String postedStatus = "";
        journalEntryRepository.updateJournalEntryStatus(id);
        transactionUtil.startTransaction();
        String sql = "Select posted_status from journal_entry where id = ?";
        Connection connection = connectionUtil.getConnection();
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setLong(1, id);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                postedStatus = resultSet.getString("posted_status");
            }
            transactionUtil.commitTransaction();
        } catch (SQLException e) {
            transactionUtil.rollbackTransaction();
        }
        Assert.assertEquals(postedStatus, PostedStatus.POSTED.name());
    }

    @Test(expected = QueryFailedException.class)
    public void testUpdateJournalEntryStatusWithInvalidId() {
        journalEntryRepository.updateJournalEntryStatus(3L);
    }

    @Test
    public void testInsertPosition() {
        int version = -1;
        Position positionDto = new Position();
        positionDto.setAccountNumber(journalEntryDto1.getAccountNumber());
        positionDto.setSecurityCusip(journalEntryDto1.getSecurityCusip());
        positionDto.setHolding((long) journalEntryDto1.getQuantity());
        transactionUtil.startTransaction();
        positionsRepository.upsertPosition(positionDto);
        String sql = "Select version from positions where account_number = ? and security_cusip = ?";
        Connection connection = connectionUtil.getConnection();
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, positionDto.getAccountNumber());
            preparedStatement.setString(2, positionDto.getSecurityCusip());
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                version = resultSet.getInt("version");
            }
            transactionUtil.commitTransaction();
        } catch (SQLException e) {
            transactionUtil.rollbackTransaction();
        }

        Assert.assertEquals(0, version);
    }

    @Test
    public void testUpsertPosition() {
        int version = -1;
        Position positionDto1 = new Position();
        positionDto1.setAccountNumber(journalEntryDto1.getAccountNumber());
        positionDto1.setSecurityCusip(journalEntryDto1.getSecurityCusip());
        positionDto1.setHolding((long) journalEntryDto1.getQuantity());
        transactionUtil.startTransaction();
        positionsRepository.upsertPosition(positionDto1);
        transactionUtil.commitTransaction();
        Position positionDto2 = new Position();
        positionDto2.setAccountNumber(journalEntryDto2.getAccountNumber());
        positionDto2.setSecurityCusip(journalEntryDto2.getSecurityCusip());
        positionDto2.setHolding((long) journalEntryDto2.getQuantity());
        transactionUtil.startTransaction();
        positionsRepository.upsertPosition(positionDto2);
        transactionUtil.commitTransaction();
        String sql = "Select version from positions where account_number = ? and security_cusip = ?";
        Connection connection = connectionUtil.getConnection();
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, positionDto1.getAccountNumber());
            preparedStatement.setString(2, positionDto1.getSecurityCusip());
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                version = resultSet.getInt("version");
            }
            transactionUtil.commitTransaction();
        } catch (SQLException e) {
            transactionUtil.rollbackTransaction();
        }

        Assert.assertEquals(1, version);
    }

    @Test(expected = OptimisticLockingException.class)
    public void testUpsertPositionWithOptimisticLock() throws Exception {
        AtomicReference<Exception> exception = new AtomicReference<>();
        Position positionDto = new Position();
        positionDto.setAccountNumber(journalEntryDto1.getAccountNumber());
        positionDto.setSecurityCusip(journalEntryDto1.getSecurityCusip());
        positionDto.setHolding((long) journalEntryDto1.getQuantity());
        positionsRepository.upsertPosition(positionDto);
        CountDownLatch countDownLatch = new CountDownLatch(threadCount);
        for (int i = 0; i < threadCount; i++) {
            executorService.submit(() -> {
                try {
                    positionsRepository.upsertPosition(positionDto);
                    countDownLatch.countDown();
                } catch (Exception e) {
                    e.printStackTrace();
                    exception.set(e);
                    System.out.println("exception detected");
                }
            });
        }
        countDownLatch.await();
        executorService.shutdownNow();
        int version = -1;
        String sql = "Select version from positions where account_number = ? and security_cusip = ?";
        Connection connection = connectionUtil.getConnection();
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, positionDto.getAccountNumber());
            preparedStatement.setString(2, positionDto.getSecurityCusip());
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                version = resultSet.getInt("version");
            }
            transactionUtil.commitTransaction();
        } catch (SQLException e) {
            transactionUtil.rollbackTransaction();
        }
        System.out.println(version);
        throw exception.get();
    }
}
