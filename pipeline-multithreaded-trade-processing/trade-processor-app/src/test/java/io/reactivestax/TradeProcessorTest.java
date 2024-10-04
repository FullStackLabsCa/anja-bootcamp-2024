package io.reactivestax;

import io.reactivestax.database.DBUtils;
import io.reactivestax.service.*;
import io.reactivestax.utility.ApplicationPropertiesUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.contrib.java.lang.system.SystemOutRule;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.*;
import java.util.Objects;
import java.util.logging.Logger;

import static org.junit.Assert.*;

public class TradeProcessorTest {
    Connection connection;
    Logger logger = Logger.getLogger(TradeProcessorTest.class.getName());

    @Before
    public void setUp() throws IOException {
        ApplicationPropertiesUtils.loadApplicationProperties();
        QueueDistributor.initializeQueue();
        ApplicationPropertiesUtils.setPortNumber("3308");
        ApplicationPropertiesUtils.setTotalNoOfLines(10000);
        ApplicationPropertiesUtils.setFilePath("/Users/Anant.Jain/source/student/anja-bootcamp-2024/pipeline-multithreaded-trade-processing/trade-processor-app/src/test/resources/trades.csv");
        ApplicationPropertiesUtils.setChunkFilePathWithName("/Users/Anant.Jain/source/student/anja-bootcamp-2024/pipeline-multithreaded-trade-processing/trade-processor-app/src/test/resources/chunks/trade_records_chunk");
        ApplicationPropertiesUtils.setChunkDirectoryPath("/Users/Anant.Jain/source/student/anja-bootcamp-2024/pipeline-multithreaded-trade-processing/trade-processor-app/src/test/resources/chunks");
        ApplicationPropertiesUtils.setNumberOfChunks(10);
        Files.createDirectories(Paths.get(ApplicationPropertiesUtils.getChunkDirectoryPath()));
        try {
            connection = DBUtils.getInstance().getConnection();
        } catch (SQLException e) {
            System.out.println("SQL Exception");
        }
    }

    @After
    public void cleanUp() throws SQLException, NullPointerException {
        String deleteFromTradePayload = "Delete from trade_payloads";
        String deleteFromJournalEntry = "Delete from journal_entry";
        String deleteFromPositions = "Delete from positions";
        try (PreparedStatement preparedStatement1 = connection.prepareStatement(deleteFromTradePayload);
             PreparedStatement preparedStatement2 = connection.prepareStatement(deleteFromJournalEntry);
             PreparedStatement preparedStatement3 = connection.prepareStatement(deleteFromPositions)) {
            preparedStatement1.execute();
            preparedStatement2.execute();
            preparedStatement3.execute();
        } catch (SQLException e) {
            System.out.println("SQL Exception");
        } finally {
            connection.close();
        }
        File directory = new File(ApplicationPropertiesUtils.getChunkDirectoryPath());
        boolean delete = false;
        File[] files = directory.listFiles();
        if (files != null) {
            for (File file : Objects.requireNonNull(directory.listFiles())) {
                delete = file.delete();
            }
        }
        if (delete) logger.info("Cleanup done");
    }

    @Rule
    public final SystemOutRule systemOutRule = new SystemOutRule().enableLog();

    @Test
    public void testFileLineCounterWithCorrectFilePath() throws IOException {
        ChunkGeneratorAndProcessorService chunkGeneratorAndProcessorService = new ChunkGeneratorAndProcessorService();
        long numberOfLines = chunkGeneratorAndProcessorService.fileLineCounter(ApplicationPropertiesUtils.getFilePath());
        assertEquals(10000, numberOfLines);
    }

    @Test(expected = IOException.class)
    public void testFileLineCounterWithWrongFilePath() throws IOException {
        ChunkGeneratorAndProcessorService chunkGeneratorAndProcessorService = new ChunkGeneratorAndProcessorService();
        chunkGeneratorAndProcessorService.fileLineCounter("wrong_file_path");
    }

    @Test
    public void testGenerateChunksWithCorrectFilePath() throws IOException, InterruptedException {
        ChunkGeneratorRunnable chunkGeneratorRunnable = new ChunkGeneratorRunnable();
        chunkGeneratorRunnable.generateChunks();
        File directory = new File(ApplicationPropertiesUtils.getChunkDirectoryPath());
        File[] files = directory.listFiles();
        if (files != null) {
            long fileCount = 0;
            for (File file : files) {
                if (file.isFile()) {
                    fileCount++;
                }
            }
            assertEquals(10, fileCount);
        }
    }

    @Test(expected = IOException.class)
    public void testGenerateChunksWithIncorrectFilePath() throws IOException, InterruptedException {
        ChunkGeneratorRunnable chunkGeneratorRunnable = new ChunkGeneratorRunnable();
        ApplicationPropertiesUtils.setFilePath("wrong_file_path");
        chunkGeneratorRunnable.generateChunks();
    }

    @Test
    public void testBuildFilePath() {
        ChunkGeneratorAndProcessorService chunkGeneratorAndProcessorService = new ChunkGeneratorAndProcessorService();
        String path = chunkGeneratorAndProcessorService.buildFilePath(1);
        assertEquals(ApplicationPropertiesUtils.getChunkDirectoryPath() + "/trade_records_chunk1.csv", path);
    }

    @Test
    public void testSetStaticValues() {
        assertEquals(10, ApplicationPropertiesUtils.getNumberOfChunks());
    }

    @Test
    public void testChunkProcessorForQueueSize() throws SQLException, IOException, InterruptedException {
        ChunkGeneratorRunnable chunkGeneratorRunnable = new ChunkGeneratorRunnable();
        chunkGeneratorRunnable.generateChunks();
        ChunkProcessor chunkProcessor = new ChunkProcessor();
        chunkProcessor.processChunk(ApplicationPropertiesUtils.getChunkDirectoryPath() + "/trade_records_chunk1.csv");
        assertFalse(QueueDistributor.getTransactionDeque(0).isEmpty());
    }

    @Test
    public void testChunkProcessorForQueueNumberWithoutMapAndRoundRobin() {
        ApplicationPropertiesUtils.setTradeDistributionUseMap(false);
        ApplicationPropertiesUtils.setTradeDistributionAlgorithm("round-robin");
        QueueDistributor.figureOutTheNextQueue("TID_000000");
        QueueDistributor.figureOutTheNextQueue("TID_000001");
        QueueDistributor.figureOutTheNextQueue("TID_000002");
        int queueNumber = QueueDistributor.figureOutTheNextQueue("TID_000003");
        assertEquals(0, queueNumber);
    }

    @Test
    public void testChunkProcessorForQueueNumberWithoutMapAndRandom() {
        ApplicationPropertiesUtils.setTradeDistributionUseMap(false);
        ApplicationPropertiesUtils.setTradeDistributionAlgorithm("random");
        QueueDistributor.figureOutTheNextQueue("TID_000000");
        QueueDistributor.figureOutTheNextQueue("TID_000001");
        QueueDistributor.figureOutTheNextQueue("TID_000002");
        int queueNumber = QueueDistributor.figureOutTheNextQueue("TID_000003");
        assertTrue(queueNumber < ApplicationPropertiesUtils.getTradeProcessorQueueCount());
    }

    @Test
    public void testChunkProcessorForQueueNumberWithMapAndRandomAndAccountNumberTradeDistribution() {
        ApplicationPropertiesUtils.setTradeDistributionUseMap(true);
        ApplicationPropertiesUtils.setTradeDistributionCriteria("accountNumber");
        ApplicationPropertiesUtils.setTradeDistributionAlgorithm("random");
        QueueDistributor.figureOutTheNextQueue("TDB_CUST_3017796");
        QueueDistributor.figureOutTheNextQueue("TDB_CUST_3017796");
        int queueNumber = QueueDistributor.figureOutTheNextQueue("TDB_CUST_3017797");
        assertEquals(queueNumber, QueueDistributor.figureOutTheNextQueue("TDB_CUST_3017797"));
    }

    @Test
    public void testChunkProcessorForQueueNumberWithMapAndRoundRobinAndTradeIdTradeDistribution() {
        ApplicationPropertiesUtils.setTradeDistributionUseMap(true);
        ApplicationPropertiesUtils.setTradeDistributionCriteria("tradeId");
        ApplicationPropertiesUtils.setTradeDistributionAlgorithm("round-robin");
        QueueDistributor.figureOutTheNextQueue("TID_000000");
        QueueDistributor.figureOutTheNextQueue("TID_000001");
        QueueDistributor.figureOutTheNextQueue("TID_000002");
        int queueNumber = QueueDistributor.figureOutTheNextQueue("TID_000003");
        assertEquals(0, queueNumber);
    }

    @Test
    public void testChunkProcessorForDatabaseInsertionOfRawPayload() throws SQLException, IOException, InterruptedException {
        ChunkGeneratorRunnable chunkGeneratorRunnable = new ChunkGeneratorRunnable();
        chunkGeneratorRunnable.generateChunks();
        int count = 0;
        ChunkProcessor chunkProcessor = new ChunkProcessor();
        chunkProcessor.processChunk(ApplicationPropertiesUtils.getChunkDirectoryPath() + "/trade_records_chunk1.csv");
        String query = "Select count(*) as count from trade_payloads";
        ResultSet resultSet = connection.prepareStatement(query).executeQuery();
        if (resultSet.next()) {
            count = resultSet.getInt("count");
        }
        assertEquals(1000, count);
    }

    @Test
    public void testQueueDistributorGiveToQueue() throws InterruptedException {
        QueueDistributor.giveToTradeQueue("TID_1234", 0);
        assertFalse(QueueDistributor.getTransactionDeque(0).isEmpty());
    }

    @Test
    public void testTradeProcessorProcessWithCorrectTradeIdForJournalEntry() throws SQLException, InterruptedException, IOException {
        ChunkGeneratorRunnable chunkGeneratorRunnable = new ChunkGeneratorRunnable();
        chunkGeneratorRunnable.generateChunks();
        ChunkProcessor chunkProcessor = new ChunkProcessor();
        chunkProcessor.processChunk(ApplicationPropertiesUtils.getChunkDirectoryPath() + "/trade_records_chunk1.csv");
        TradeProcessor tradeProcessor = new TradeProcessor(QueueDistributor.getTransactionDeque(0));
        tradeProcessor.setConnection(connection);
        tradeProcessor.processTrade("TDB_00000995");
        String query = "Select account_number from journal_entry where trade_id = 'TDB_00000995'";
        String accountNumber = "";
        ResultSet resultSet = connection.prepareStatement(query).executeQuery();
        if (resultSet.next()) {
            accountNumber = resultSet.getString("account_number");
        }
        assertEquals("TDB_CUST_5423076", accountNumber);
    }

    @Test
    public void testTradeProcessorProcessForLookupStatusWithCusipNotPresentInSecuritiesReference() throws SQLException, InterruptedException, IOException {
        ChunkGeneratorRunnable chunkGeneratorRunnable = new ChunkGeneratorRunnable();
        chunkGeneratorRunnable.generateChunks();
        ChunkProcessor chunkProcessor = new ChunkProcessor();
        chunkProcessor.processChunk(ApplicationPropertiesUtils.getChunkDirectoryPath() + "/trade_records_chunk1.csv");
        TradeProcessor tradeProcessor = new TradeProcessor(QueueDistributor.getTransactionDeque(0));
        tradeProcessor.setConnection(connection);
        tradeProcessor.processTrade("TDB_00000000");
        String query = "Select lookup_status from trade_payloads where trade_id = 'TDB_00000000'";
        String lookupStatus = "";
        ResultSet resultSet = connection.prepareStatement(query).executeQuery();
        if (resultSet.next()) {
            lookupStatus = resultSet.getString("lookup_status");
        }
        assertEquals("fail", lookupStatus);
    }

    @Test
    public void testTradeProcessorProcessForLookupStatusWithCusipPresentInSecuritiesReference() throws SQLException, InterruptedException, IOException {
        ChunkGeneratorRunnable chunkGeneratorRunnable = new ChunkGeneratorRunnable();
        chunkGeneratorRunnable.generateChunks();
        ChunkProcessor chunkProcessor = new ChunkProcessor();
        chunkProcessor.processChunk(ApplicationPropertiesUtils.getChunkDirectoryPath() + "/trade_records_chunk1.csv");
        TradeProcessor tradeProcessor = new TradeProcessor(QueueDistributor.getTransactionDeque(0));
        tradeProcessor.setConnection(connection);
        tradeProcessor.processTrade("TDB_00000002");
        String query = "Select lookup_status from trade_payloads where trade_id = 'TDB_00000002'";
        String lookupStatus = "";
        ResultSet resultSet = connection.prepareStatement(query).executeQuery();
        if (resultSet.next()) {
            lookupStatus = resultSet.getString("lookup_status");
        }
        assertEquals("pass", lookupStatus);
    }

    @Test
    public void testTradeProcessorProcessForPostedStatusWithCusipNotPresentInSecuritiesReference() throws SQLException, InterruptedException, IOException {
        ChunkGeneratorRunnable chunkGeneratorRunnable = new ChunkGeneratorRunnable();
        chunkGeneratorRunnable.generateChunks();
        ChunkProcessor chunkProcessor = new ChunkProcessor();
        chunkProcessor.processChunk(ApplicationPropertiesUtils.getChunkDirectoryPath() + "/trade_records_chunk1.csv");
        TradeProcessor tradeProcessor = new TradeProcessor(QueueDistributor.getTransactionDeque(0));
        tradeProcessor.setConnection(connection);
        tradeProcessor.processTrade("TDB_00000002");
        String query = "Select je_status from trade_payloads where trade_id = 'TDB_00000000'";
        String postedStatus = "";
        ResultSet resultSet = connection.prepareStatement(query).executeQuery();
        if (resultSet.next()) {
            postedStatus = resultSet.getString("je_status");
        }
        assertEquals("not_posted", postedStatus);
    }

    @Test
    public void testTradeProcessorProcessForPostedStatusWithCusipPresentInSecuritiesReference() throws SQLException, InterruptedException, IOException {
        ChunkGeneratorRunnable chunkGeneratorRunnable = new ChunkGeneratorRunnable();
        chunkGeneratorRunnable.generateChunks();
        ChunkProcessor chunkProcessor = new ChunkProcessor();
        chunkProcessor.processChunk(ApplicationPropertiesUtils.getChunkDirectoryPath() + "/trade_records_chunk1.csv");
        TradeProcessor tradeProcessor = new TradeProcessor(QueueDistributor.getTransactionDeque(0));
        tradeProcessor.setConnection(connection);
        tradeProcessor.processTrade("TDB_00000002");
        String query = "Select je_status from trade_payloads where trade_id = 'TDB_00000002'";
        String postedStatus = "";
        ResultSet resultSet = connection.prepareStatement(query).executeQuery();
        if (resultSet.next()) {
            postedStatus = resultSet.getString("je_status");
        }
        assertEquals("posted", postedStatus);
    }

    @Test
    public void testTradeProcessorProcessWithCorrectTradeIdForPositionsEntryInsert() throws SQLException, InterruptedException, IOException {
        ChunkGeneratorRunnable chunkGeneratorRunnable = new ChunkGeneratorRunnable();
        chunkGeneratorRunnable.generateChunks();
        ChunkProcessor chunkProcessor = new ChunkProcessor();
        chunkProcessor.processChunk(ApplicationPropertiesUtils.getChunkDirectoryPath() + "/trade_records_chunk1.csv");
        TradeProcessor tradeProcessor = new TradeProcessor(QueueDistributor.getTransactionDeque(0));
        tradeProcessor.setConnection(connection);
        tradeProcessor.processTrade("TDB_00000995");
        String query = "Select positions from positions where account_number='TDB_CUST_5423076' and " +
                "security_cusip='MSFT'";
        int positions = 0;
        ResultSet resultSet = connection.prepareStatement(query).executeQuery();
        if (resultSet.next()) {
            positions = resultSet.getInt("positions");
        }
        assertEquals(-830, positions);
    }

    @Test
    public void testTradeProcessorProcessWithCorrectTradeIdForPositionsEntryUpdate() throws SQLException, InterruptedException, IOException {
        ChunkGeneratorRunnable chunkGeneratorRunnable = new ChunkGeneratorRunnable();
        chunkGeneratorRunnable.generateChunks();
        ChunkProcessor chunkProcessor = new ChunkProcessor();
        chunkProcessor.processChunk(ApplicationPropertiesUtils.getChunkDirectoryPath() + "/trade_records_chunk1.csv");
        TradeProcessor tradeProcessor = new TradeProcessor(QueueDistributor.getTransactionDeque(0));
        tradeProcessor.setConnection(connection);
        tradeProcessor.processTrade("TDB_00000001");
        tradeProcessor.processTrade("TDB_00000002");
        String query = "Select version from positions where account_number='TDB_CUST_2517563' and " +
                "security_cusip='TSLA'";
        int version = 0;
        ResultSet resultSet = connection.prepareStatement(query).executeQuery();
        if (resultSet.next()) {
            version = resultSet.getInt("version");
        }
        assertEquals(2, version);
    }

    @Test
    public void testTradeProcessorRetryTransactionDeadLetterQueue() throws InterruptedException {
        TradeProcessor tradeProcessor = new TradeProcessor(QueueDistributor.getTransactionDeque(0));
        tradeProcessor.retryTransaction("TDB_00001000");
        tradeProcessor.retryTransaction("TDB_00001000");
        tradeProcessor.retryTransaction("TDB_00001000");
        tradeProcessor.retryTransaction("TDB_00001000");
        assertFalse(tradeProcessor.getTradeDeque().isEmpty());
    }

    @Test
    public void testTradeProcessorRetryTransactionRetryCountMap() throws InterruptedException {
        TradeProcessor tradeProcessor = new TradeProcessor(QueueDistributor.getTransactionDeque(0));
        tradeProcessor.retryTransaction("TDB_00001000");
        tradeProcessor.retryTransaction("TDB_00001000");
        assertEquals(1, tradeProcessor.getRetryCountMap().size());
    }
}