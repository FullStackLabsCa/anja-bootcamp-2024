package io.reactivestax;

import com.zaxxer.hikari.HikariDataSource;
import io.reactivestax.database.DatabaseConnection;
import io.reactivestax.service.*;
import io.reactivestax.utility.MaintainStaticValues;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.contrib.java.lang.system.SystemOutRule;

import java.io.File;
import java.io.IOException;
import java.sql.*;
import java.util.Objects;
import java.util.logging.Logger;

import static org.junit.Assert.*;

public class TradeProcessorTest {
    HikariDataSource dataSource;
    Connection connection;
    Logger logger = Logger.getLogger(TradeProcessorTest.class.getName());

    @Before
    public void setUp() {
        ChunkGeneratorAndProcessorService chunkGeneratorAndProcessorService = new ChunkGeneratorAndProcessorService();
        chunkGeneratorAndProcessorService.setStaticValues();
        QueueDistributor.initializeQueue();
        MaintainStaticValues.setPortNumber("3308");
        MaintainStaticValues.setTotalNoOfLines(10000);
        MaintainStaticValues.setFilePath("/Users/Anant.Jain/source/student/anja-bootcamp-2024/pipeline-multithreaded-trade-processing/trade-processor-app/src/test/resources/trades.csv");
        MaintainStaticValues.setChunkFilePathWithName("/Users/Anant.Jain/source/student/anja-bootcamp-2024/pipeline-multithreaded-trade-processing/trade-processor-app/src/test/resources/chunks/trade_records_chunk");
        MaintainStaticValues.setChunkDirectoryPath("/Users/Anant.Jain/source/student/anja-bootcamp-2024/pipeline-multithreaded-trade-processing/trade-processor-app/src/test/resources/chunks");
        dataSource = DatabaseConnection.configureHikariCP(MaintainStaticValues.getPortNumber(),
                MaintainStaticValues.getDbName(), MaintainStaticValues.getUsername(), MaintainStaticValues.getPassword());
        try {
            connection = dataSource.getConnection();
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
            dataSource.close();
        }
        File directory = new File(MaintainStaticValues.getChunkDirectoryPath());
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
        long numberOfLines = chunkGeneratorAndProcessorService.fileLineCounter(MaintainStaticValues.getFilePath());
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
        File directory = new File(MaintainStaticValues.getChunkDirectoryPath());
        File[] files = directory.listFiles();
        if (files != null) {
            long fileCount = 0;
            for (File file : files) {
                if (file.isFile()) {
                    fileCount++;
                }
            }
            assertEquals(1, fileCount);
        }
    }

    @Test(expected = IOException.class)
    public void testGenerateChunksWithIncorrectFilePath() throws IOException, InterruptedException {
        ChunkGeneratorRunnable chunkGeneratorRunnable = new ChunkGeneratorRunnable();
        MaintainStaticValues.setFilePath("wrong_file_path");
        chunkGeneratorRunnable.generateChunks();
    }

    @Test
    public void testBuildFilePath() {
        ChunkGeneratorAndProcessorService chunkGeneratorAndProcessorService = new ChunkGeneratorAndProcessorService();
        String path = chunkGeneratorAndProcessorService.buildFilePath(1);
        assertEquals(MaintainStaticValues.getChunkDirectoryPath() + "/trade_records_chunk1.csv", path);
    }

    @Test
    public void testSetStaticValues() {
        assertEquals(1, MaintainStaticValues.getNumberOfChunks());
    }

    @Test
    public void testChunkProcessorForQueueSize() throws SQLException {
        ChunkProcessor chunkProcessor = new ChunkProcessor(dataSource);
        chunkProcessor.processChunk(MaintainStaticValues.getChunkDirectoryPath() + "/trade_records_chunk1.csv");
        assertFalse(QueueDistributor.getTransactionDeque(0).isEmpty());
    }

    @Test
    public void testChunkProcessorForQueueNumber() {
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
        ChunkProcessor chunkProcessor = new ChunkProcessor(dataSource);
        chunkProcessor.processChunk(MaintainStaticValues.getChunkDirectoryPath() + "/trade_records_chunk1.csv");
        String query = "Select count(*) as count from trade_payloads";
        ResultSet resultSet = connection.prepareStatement(query).executeQuery();
        if (resultSet.next()) {
            count = resultSet.getInt("count");
        }
        assertEquals(10000, count);
    }


    @Test(expected = NullPointerException.class)
    public void testChunkProcessorWithNullDataSource() throws SQLException {
        ChunkProcessor chunkProcessor = new ChunkProcessor(null);
        chunkProcessor.processChunk("wrong_file_path");
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
        ChunkProcessor chunkProcessor = new ChunkProcessor(dataSource);
        chunkProcessor.processChunk(MaintainStaticValues.getChunkDirectoryPath() + "/trade_records_chunk1.csv");
        TradeProcessor tradeProcessor = new TradeProcessor(QueueDistributor.getTransactionDeque(0), dataSource);
        tradeProcessor.processTrade("TDB_00001000");
        String query = "Select account_number from journal_entry where trade_id = 'TDB_00001000'";
        String accountNumber = "";
        ResultSet resultSet = connection.prepareStatement(query).executeQuery();
        if (resultSet.next()) {
            accountNumber = resultSet.getString("account_number");
        }
        assertEquals("TDB_CUST_6635059", accountNumber);
    }

    @Test
    public void testTradeProcessorProcessWithCorrectTradeIdForPositionsEntryInsert() throws SQLException, InterruptedException, IOException {
        ChunkGeneratorRunnable chunkGeneratorRunnable = new ChunkGeneratorRunnable();
        chunkGeneratorRunnable.generateChunks();
        ChunkProcessor chunkProcessor = new ChunkProcessor(dataSource);
        chunkProcessor.processChunk(MaintainStaticValues.getChunkDirectoryPath() + "/trade_records_chunk1.csv");
        TradeProcessor tradeProcessor = new TradeProcessor(QueueDistributor.getTransactionDeque(0), dataSource);
        tradeProcessor.processTrade("TDB_00001000");
        String query = "Select positions from positions where account_number='TDB_CUST_6635059' and security_cusip='NFLX'";
        int positions = 0;
        ResultSet resultSet = connection.prepareStatement(query).executeQuery();
        if (resultSet.next()) {
            positions = resultSet.getInt("positions");
        }
        assertEquals(-137, positions);
    }

    @Test
    public void testTradeProcessorProcessWithCorrectTradeIdForPositionsEntryUpdate() throws SQLException, InterruptedException, IOException {
        ChunkGeneratorRunnable chunkGeneratorRunnable = new ChunkGeneratorRunnable();
        chunkGeneratorRunnable.generateChunks();
        ChunkProcessor chunkProcessor = new ChunkProcessor(dataSource);
        chunkProcessor.processChunk(MaintainStaticValues.getChunkDirectoryPath() + "/trade_records_chunk1.csv");
        TradeProcessor tradeProcessor = new TradeProcessor(QueueDistributor.getTransactionDeque(0), dataSource);
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
        TradeProcessor tradeProcessor = new TradeProcessor(QueueDistributor.getTransactionDeque(0), dataSource);
        tradeProcessor.retryTransaction("TDB_00001000");
        tradeProcessor.retryTransaction("TDB_00001000");
        tradeProcessor.retryTransaction("TDB_00001000");
        tradeProcessor.retryTransaction("TDB_00001000");
        assertFalse(tradeProcessor.getTradeDeque().isEmpty());
    }

    @Test
    public void testTradeProcessorRetryTransactionRetryCountMap() throws InterruptedException {
        TradeProcessor tradeProcessor = new TradeProcessor(QueueDistributor.getTransactionDeque(0), dataSource);
        tradeProcessor.retryTransaction("TDB_00001000");
        tradeProcessor.retryTransaction("TDB_00001000");
        assertEquals(1, tradeProcessor.getRetryCountMap().size());
    }
}