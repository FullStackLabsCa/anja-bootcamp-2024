//package io.reactivestax;
//
//import io.reactivestax.database.DBUtils;
//import io.reactivestax.service.*;
//import io.reactivestax.utility.ApplicationPropertiesUtils;
//import org.junit.After;
//import org.junit.Before;
//import org.junit.Rule;
//import org.junit.Test;
//import org.junit.contrib.java.lang.system.SystemOutRule;
//
//import java.io.File;
//import java.io.IOException;
//import java.nio.file.Files;
//import java.nio.file.Paths;
//import java.sql.*;
//import java.util.Objects;
//import java.util.logging.Logger;
//
//import static org.junit.Assert.*;
//
//public class TradeProcessorTest {
//    Connection connection;
//    Logger logger = Logger.getLogger(TradeProcessorTest.class.getName());
//    ApplicationPropertiesUtils applicationTestWithAccNoMapFalseRandom;
//    ApplicationPropertiesUtils applicationTestWithAccNoMapFalseRdRobin;
//    ApplicationPropertiesUtils applicationTestWithAccNoMapTrueRandom;
//    ApplicationPropertiesUtils applicationTestWithAccNoMapTrueRdRobin;
//    ApplicationPropertiesUtils applicationTestWithTradeIdMapTrueRandom;
//    ApplicationPropertiesUtils applicationTestWithTradeIdMapTrueRdRobin;
//    ApplicationPropertiesUtils applicationTestWrongFilePath;
//
//    public TradeProcessorTest(){
//        applicationTestWithAccNoMapFalseRandom = new ApplicationPropertiesUtils("applicationTestWithAccNoMapFalseRandom.properties");
//        applicationTestWithAccNoMapFalseRdRobin = new ApplicationPropertiesUtils("applicationTestWithAccNoMapFalseRdRobin.properties");
//        applicationTestWithAccNoMapTrueRandom = new ApplicationPropertiesUtils("applicationTestWithAccNoMapTrueRandom.properties");
//        applicationTestWithAccNoMapTrueRdRobin = new ApplicationPropertiesUtils("applicationTestWithAccNoMapTrueRdRobin.properties");
//        applicationTestWithTradeIdMapTrueRandom = new ApplicationPropertiesUtils("applicationTestWithTradeIdMapTrueRandom.properties");
//        applicationTestWithTradeIdMapTrueRdRobin = new ApplicationPropertiesUtils("applicationTestWithTradeIdMapTrueRdRobin.properties");
//        applicationTestWrongFilePath = new ApplicationPropertiesUtils("applicationTestWrongFilePath.properties");
//        QueueDistributor.initializeQueue(applicationTestWithTradeIdMapTrueRandom.getTradeProcessorQueueCount());
//        applicationTestWithTradeIdMapTrueRandom.setTotalNoOfLines(10000);
//    }
//
//    @Before
//    public void setUp() throws IOException {
//        Files.createDirectories(Paths.get(applicationTestWithTradeIdMapTrueRandom.getChunkDirectoryPath()));
//        try {
//            connection = DBUtils.getInstance(applicationTestWithTradeIdMapTrueRandom).getConnection();
//        } catch (SQLException e) {
//            System.out.println("SQL Exception");
//        }
//    }
//
//    @After
//    public void cleanUp() throws SQLException, NullPointerException {
//        String deleteFromTradePayload = "Delete from trade_payloads";
//        String deleteFromJournalEntry = "Delete from journal_entry";
//        String deleteFromPositions = "Delete from positions";
//        try (PreparedStatement preparedStatement1 = connection.prepareStatement(deleteFromTradePayload);
//             PreparedStatement preparedStatement2 = connection.prepareStatement(deleteFromJournalEntry);
//             PreparedStatement preparedStatement3 = connection.prepareStatement(deleteFromPositions)) {
//            preparedStatement1.execute();
//            preparedStatement2.execute();
//            preparedStatement3.execute();
//        } catch (SQLException e) {
//            System.out.println("SQL Exception");
//        } finally {
//            connection.close();
//        }
//        File directory = new File(applicationTestWithTradeIdMapTrueRandom.getChunkDirectoryPath());
//        boolean delete = false;
//        File[] files = directory.listFiles();
//        if (files != null) {
//            for (File file : Objects.requireNonNull(directory.listFiles())) {
//                delete = file.delete();
//            }
//        }
//        if (delete) logger.info("Cleanup done");
//    }
//
//    @Rule
//    public final SystemOutRule systemOutRule = new SystemOutRule().enableLog();
//
//    @Test
//    public void testFileLineCounterWithCorrectFilePath() throws IOException {
//        ChunkGeneratorAndProcessorService chunkGeneratorAndProcessorService = new ChunkGeneratorAndProcessorService();
//        long numberOfLines = chunkGeneratorAndProcessorService.fileLineCounter(applicationTestWithTradeIdMapTrueRandom.getFilePath());
//        assertEquals(10000, numberOfLines);
//    }
//
//    @Test(expected = IOException.class)
//    public void testFileLineCounterWithWrongFilePath() throws IOException {
//        ChunkGeneratorAndProcessorService chunkGeneratorAndProcessorService = new ChunkGeneratorAndProcessorService();
//        chunkGeneratorAndProcessorService.fileLineCounter("wrong_file_path");
//    }
//
//    @Test
//    public void testGenerateChunksWithCorrectFilePath() throws IOException, InterruptedException {
//        ChunkGeneratorRunnable chunkGeneratorRunnable = new ChunkGeneratorRunnable(applicationTestWithTradeIdMapTrueRandom);
//        chunkGeneratorRunnable.generateChunks();
//        File directory = new File(applicationTestWithTradeIdMapTrueRandom.getChunkDirectoryPath());
//        File[] files = directory.listFiles();
//        if (files != null) {
//            long fileCount = 0;
//            for (File file : files) {
//                if (file.isFile()) {
//                    fileCount++;
//                }
//            }
//            assertEquals(10, fileCount);
//        }
//    }
//
//    @Test(expected = IOException.class)
//    public void testGenerateChunksWithIncorrectFilePath() throws IOException, InterruptedException {
//        ChunkGeneratorRunnable chunkGeneratorRunnable = new ChunkGeneratorRunnable(applicationTestWrongFilePath);
//        chunkGeneratorRunnable.generateChunks();
//    }
//
//    @Test
//    public void testBuildFilePath() {
//        ChunkGeneratorAndProcessorService chunkGeneratorAndProcessorService = new ChunkGeneratorAndProcessorService();
//        String path = chunkGeneratorAndProcessorService.buildFilePath(1, applicationTestWithTradeIdMapTrueRandom.getChunkFilePathWithName());
//        assertEquals(applicationTestWithTradeIdMapTrueRandom.getChunkDirectoryPath() + "/trade_records_chunk1.csv", path);
//    }
//
//    @Test
//    public void testSetStaticValues() {
//        assertEquals(10, applicationTestWithTradeIdMapTrueRandom.getNumberOfChunks());
//    }
//
//    @Test
//    public void testChunkProcessorForQueueSize() throws SQLException, IOException, InterruptedException {
//        ChunkGeneratorRunnable chunkGeneratorRunnable = new ChunkGeneratorRunnable(applicationTestWithTradeIdMapTrueRandom);
//        chunkGeneratorRunnable.generateChunks();
//        ChunkProcessor chunkProcessor = new ChunkProcessor(applicationTestWithTradeIdMapTrueRandom);
//        chunkProcessor.processChunk(applicationTestWithTradeIdMapTrueRandom.getChunkDirectoryPath() + "/trade_records_chunk1.csv");
//        assertFalse(QueueDistributor.getTransactionDeque(0).isEmpty());
//    }
//
//    @Test
//    public void testChunkProcessorForQueueNumberWithoutMapAndRoundRobin() {
//        QueueDistributor.figureOutTheNextQueue("TID_000000", applicationTestWithAccNoMapFalseRdRobin.isTradeDistributionUseMap(),
//                applicationTestWithAccNoMapFalseRdRobin.getTradeDistributionAlgorithm(), applicationTestWithAccNoMapFalseRdRobin.getTradeProcessorQueueCount());
//        QueueDistributor.figureOutTheNextQueue("TID_000001", applicationTestWithAccNoMapFalseRdRobin.isTradeDistributionUseMap(),
//                applicationTestWithAccNoMapFalseRdRobin.getTradeDistributionAlgorithm(), applicationTestWithAccNoMapFalseRdRobin.getTradeProcessorQueueCount());
//        QueueDistributor.figureOutTheNextQueue("TID_000002", applicationTestWithAccNoMapFalseRdRobin.isTradeDistributionUseMap(),
//                applicationTestWithAccNoMapFalseRdRobin.getTradeDistributionAlgorithm(), applicationTestWithAccNoMapFalseRdRobin.getTradeProcessorQueueCount());
//        int queueNumber = QueueDistributor.figureOutTheNextQueue("TID_000003", applicationTestWithAccNoMapFalseRdRobin.isTradeDistributionUseMap(),
//                applicationTestWithAccNoMapFalseRdRobin.getTradeDistributionAlgorithm(), applicationTestWithAccNoMapFalseRdRobin.getTradeProcessorQueueCount());
//        assertEquals(0, queueNumber);
//    }
//
//    @Test
//    public void testChunkProcessorForQueueNumberWithoutMapAndRandom() {
//        QueueDistributor.figureOutTheNextQueue("TID_000000",
//                applicationTestWithAccNoMapFalseRandom.isTradeDistributionUseMap(),
//                applicationTestWithAccNoMapFalseRandom.getTradeDistributionAlgorithm(),
//                applicationTestWithAccNoMapFalseRandom.getTradeProcessorQueueCount());
//        QueueDistributor.figureOutTheNextQueue("TID_000001",
//                applicationTestWithAccNoMapFalseRandom.isTradeDistributionUseMap(),
//                applicationTestWithAccNoMapFalseRandom.getTradeDistributionAlgorithm(),
//                applicationTestWithAccNoMapFalseRandom.getTradeProcessorQueueCount());
//        QueueDistributor.figureOutTheNextQueue("TID_000002",
//                applicationTestWithAccNoMapFalseRandom.isTradeDistributionUseMap(),
//                applicationTestWithAccNoMapFalseRandom.getTradeDistributionAlgorithm(),
//                applicationTestWithAccNoMapFalseRandom.getTradeProcessorQueueCount());
//        int queueNumber = QueueDistributor.figureOutTheNextQueue("TID_000003",
//                applicationTestWithAccNoMapFalseRandom.isTradeDistributionUseMap(),
//                applicationTestWithAccNoMapFalseRandom.getTradeDistributionAlgorithm(),
//                applicationTestWithAccNoMapFalseRandom.getTradeProcessorQueueCount());
//        assertTrue(queueNumber < applicationTestWithAccNoMapFalseRandom.getTradeProcessorQueueCount());
//    }
//
//    @Test
//    public void testChunkProcessorForQueueNumberWithMapAndRandomAndAccountNumberTradeDistribution() {
//        QueueDistributor.figureOutTheNextQueue("TDB_CUST_3017796",
//                applicationTestWithAccNoMapTrueRandom.isTradeDistributionUseMap(),
//                applicationTestWithAccNoMapTrueRandom.getTradeDistributionAlgorithm(),
//                applicationTestWithAccNoMapTrueRandom.getTradeProcessorQueueCount());
//        QueueDistributor.figureOutTheNextQueue("TDB_CUST_3017796",
//                applicationTestWithAccNoMapTrueRandom.isTradeDistributionUseMap(),
//                applicationTestWithAccNoMapTrueRandom.getTradeDistributionAlgorithm(),
//                applicationTestWithAccNoMapTrueRandom.getTradeProcessorQueueCount());
//        int queueNumber = QueueDistributor.figureOutTheNextQueue("TDB_CUST_3017797",
//                applicationTestWithAccNoMapTrueRandom.isTradeDistributionUseMap(),
//                applicationTestWithAccNoMapTrueRandom.getTradeDistributionAlgorithm(),
//                applicationTestWithAccNoMapTrueRandom.getTradeProcessorQueueCount());
//        assertEquals(queueNumber, QueueDistributor.figureOutTheNextQueue("TDB_CUST_3017797",
//                applicationTestWithAccNoMapTrueRandom.isTradeDistributionUseMap(),
//                applicationTestWithAccNoMapTrueRandom.getTradeDistributionAlgorithm(),
//                applicationTestWithAccNoMapTrueRandom.getTradeProcessorQueueCount()));
//    }
//
//    @Test
//    public void testChunkProcessorForQueueNumberWithMapAndRoundRobinAndTradeIdTradeDistribution() {
//        QueueDistributor.figureOutTheNextQueue("TID_000000",
//                applicationTestWithTradeIdMapTrueRdRobin.isTradeDistributionUseMap(),
//                applicationTestWithTradeIdMapTrueRdRobin.getTradeDistributionAlgorithm(),
//                applicationTestWithTradeIdMapTrueRdRobin.getTradeProcessorQueueCount());
//        QueueDistributor.figureOutTheNextQueue("TID_000001",
//                applicationTestWithTradeIdMapTrueRdRobin.isTradeDistributionUseMap(),
//                applicationTestWithTradeIdMapTrueRdRobin.getTradeDistributionAlgorithm(),
//                applicationTestWithTradeIdMapTrueRdRobin.getTradeProcessorQueueCount());
//        QueueDistributor.figureOutTheNextQueue("TID_000002",
//                applicationTestWithTradeIdMapTrueRdRobin.isTradeDistributionUseMap(),
//                applicationTestWithTradeIdMapTrueRdRobin.getTradeDistributionAlgorithm(),
//                applicationTestWithTradeIdMapTrueRdRobin.getTradeProcessorQueueCount());
//        int queueNumber = QueueDistributor.figureOutTheNextQueue("TID_000003",
//                applicationTestWithTradeIdMapTrueRdRobin.isTradeDistributionUseMap(),
//                applicationTestWithTradeIdMapTrueRdRobin.getTradeDistributionAlgorithm(),
//                applicationTestWithTradeIdMapTrueRdRobin.getTradeProcessorQueueCount());
//        assertEquals(0, queueNumber);
//    }
//
//    @Test
//    public void testChunkProcessorForDatabaseInsertionOfRawPayload() throws SQLException, IOException, InterruptedException {
//        ChunkGeneratorRunnable chunkGeneratorRunnable = new ChunkGeneratorRunnable(applicationTestWithAccNoMapTrueRdRobin);
//        applicationTestWithAccNoMapTrueRdRobin.setTotalNoOfLines(10000);
//        chunkGeneratorRunnable.generateChunks();
//        int count = 0;
//        ChunkProcessor chunkProcessor = new ChunkProcessor(applicationTestWithAccNoMapTrueRdRobin);
//        chunkProcessor.processChunk(applicationTestWithAccNoMapTrueRdRobin.getChunkDirectoryPath() + "/trade_records_chunk1.csv");
//        String query = "Select count(*) as count from trade_payloads";
//        ResultSet resultSet = connection.prepareStatement(query).executeQuery();
//        if (resultSet.next()) {
//            count = resultSet.getInt("count");
//        }
//        assertEquals(1000, count);
//    }
//
//    @Test
//    public void testQueueDistributorGiveToQueue() throws InterruptedException {
//        QueueDistributor.giveToTradeQueue("TID_1234", 0);
//        assertFalse(QueueDistributor.getTransactionDeque(0).isEmpty());
//    }
//
//    @Test
//    public void testTradeProcessorProcessWithCorrectTradeIdForJournalEntry() throws SQLException, InterruptedException, IOException {
//        ChunkGeneratorRunnable chunkGeneratorRunnable = new ChunkGeneratorRunnable(applicationTestWithAccNoMapTrueRdRobin);
//        applicationTestWithAccNoMapTrueRdRobin.setTotalNoOfLines(10000);
//        chunkGeneratorRunnable.generateChunks();
//        ChunkProcessor chunkProcessor = new ChunkProcessor(applicationTestWithAccNoMapTrueRdRobin);
//        chunkProcessor.processChunk(applicationTestWithAccNoMapTrueRdRobin.getChunkDirectoryPath() + "/trade_records_chunk1.csv");
//        TradeProcessor tradeProcessor = new TradeProcessor(QueueDistributor.getTransactionDeque(0), applicationTestWithAccNoMapTrueRdRobin);
//        tradeProcessor.setConnection(connection);
//        tradeProcessor.processTrade("TDB_00000995");
//        String query = "Select account_number from journal_entry where trade_id = 'TDB_00000995'";
//        String accountNumber = "";
//        ResultSet resultSet = connection.prepareStatement(query).executeQuery();
//        if (resultSet.next()) {
//            accountNumber = resultSet.getString("account_number");
//        }
//        assertEquals("TDB_CUST_5423076", accountNumber);
//    }
//
//    @Test
//    public void testTradeProcessorProcessForLookupStatusWithCusipNotPresentInSecuritiesReference() throws SQLException, InterruptedException, IOException {
//        ChunkGeneratorRunnable chunkGeneratorRunnable = new ChunkGeneratorRunnable(applicationTestWithAccNoMapTrueRdRobin);
//        applicationTestWithAccNoMapTrueRdRobin.setTotalNoOfLines(10000);
//        chunkGeneratorRunnable.generateChunks();
//        ChunkProcessor chunkProcessor = new ChunkProcessor(applicationTestWithAccNoMapTrueRdRobin);
//        chunkProcessor.processChunk(applicationTestWithAccNoMapTrueRdRobin.getChunkDirectoryPath() + "/trade_records_chunk1.csv");
//        TradeProcessor tradeProcessor = new TradeProcessor(QueueDistributor.getTransactionDeque(0), applicationTestWithAccNoMapTrueRdRobin);
//        tradeProcessor.setConnection(connection);
//        tradeProcessor.processTrade("TDB_00000000");
//        String query = "Select lookup_status from trade_payloads where trade_id = 'TDB_00000000'";
//        String lookupStatus = "";
//        ResultSet resultSet = connection.prepareStatement(query).executeQuery();
//        if (resultSet.next()) {
//            lookupStatus = resultSet.getString("lookup_status");
//        }
//        assertEquals("fail", lookupStatus);
//    }
//
//    @Test
//    public void testTradeProcessorProcessForLookupStatusWithCusipPresentInSecuritiesReference() throws SQLException, InterruptedException, IOException {
//        ChunkGeneratorRunnable chunkGeneratorRunnable = new ChunkGeneratorRunnable(applicationTestWithAccNoMapTrueRdRobin);
//        applicationTestWithAccNoMapTrueRdRobin.setTotalNoOfLines(10000);
//        chunkGeneratorRunnable.generateChunks();
//        ChunkProcessor chunkProcessor = new ChunkProcessor(applicationTestWithAccNoMapTrueRdRobin);
//        chunkProcessor.processChunk(applicationTestWithAccNoMapTrueRdRobin.getChunkDirectoryPath() + "/trade_records_chunk1.csv");
//        TradeProcessor tradeProcessor = new TradeProcessor(QueueDistributor.getTransactionDeque(0), applicationTestWithAccNoMapTrueRdRobin);
//        tradeProcessor.setConnection(connection);
//        tradeProcessor.processTrade("TDB_00000002");
//        String query = "Select lookup_status from trade_payloads where trade_id = 'TDB_00000002'";
//        String lookupStatus = "";
//        ResultSet resultSet = connection.prepareStatement(query).executeQuery();
//        if (resultSet.next()) {
//            lookupStatus = resultSet.getString("lookup_status");
//        }
//        assertEquals("pass", lookupStatus);
//    }
//
//    @Test
//    public void testTradeProcessorProcessForPostedStatusWithCusipNotPresentInSecuritiesReference() throws SQLException, InterruptedException, IOException {
//        ChunkGeneratorRunnable chunkGeneratorRunnable = new ChunkGeneratorRunnable(applicationTestWithAccNoMapTrueRdRobin);
//        applicationTestWithAccNoMapTrueRdRobin.setTotalNoOfLines(10000);
//        chunkGeneratorRunnable.generateChunks();
//        ChunkProcessor chunkProcessor = new ChunkProcessor(applicationTestWithAccNoMapTrueRdRobin);
//        chunkProcessor.processChunk(applicationTestWithAccNoMapTrueRdRobin.getChunkDirectoryPath() + "/trade_records_chunk1.csv");
//        TradeProcessor tradeProcessor = new TradeProcessor(QueueDistributor.getTransactionDeque(0), applicationTestWithAccNoMapTrueRdRobin);
//        tradeProcessor.setConnection(connection);
//        tradeProcessor.processTrade("TDB_00000002");
//        String query = "Select je_status from trade_payloads where trade_id = 'TDB_00000000'";
//        String postedStatus = "";
//        ResultSet resultSet = connection.prepareStatement(query).executeQuery();
//        if (resultSet.next()) {
//            postedStatus = resultSet.getString("je_status");
//        }
//        assertEquals("not_posted", postedStatus);
//    }
//
//    @Test
//    public void testTradeProcessorProcessForPostedStatusWithCusipPresentInSecuritiesReference() throws SQLException, InterruptedException, IOException {
//        ChunkGeneratorRunnable chunkGeneratorRunnable = new ChunkGeneratorRunnable(applicationTestWithAccNoMapTrueRdRobin);
//        applicationTestWithAccNoMapTrueRdRobin.setTotalNoOfLines(10000);
//        chunkGeneratorRunnable.generateChunks();
//        ChunkProcessor chunkProcessor = new ChunkProcessor(applicationTestWithAccNoMapTrueRdRobin);
//        chunkProcessor.processChunk(applicationTestWithAccNoMapTrueRdRobin.getChunkDirectoryPath() + "/trade_records_chunk1.csv");
//        TradeProcessor tradeProcessor = new TradeProcessor(QueueDistributor.getTransactionDeque(0), applicationTestWithAccNoMapTrueRdRobin);
//        tradeProcessor.setConnection(connection);
//        tradeProcessor.processTrade("TDB_00000002");
//        String query = "Select je_status from trade_payloads where trade_id = 'TDB_00000002'";
//        String postedStatus = "";
//        ResultSet resultSet = connection.prepareStatement(query).executeQuery();
//        if (resultSet.next()) {
//            postedStatus = resultSet.getString("je_status");
//        }
//        assertEquals("posted", postedStatus);
//    }
//
//    @Test
//    public void testTradeProcessorProcessWithCorrectTradeIdForPositionsEntryInsert() throws SQLException, InterruptedException, IOException {
//        ChunkGeneratorRunnable chunkGeneratorRunnable = new ChunkGeneratorRunnable(applicationTestWithAccNoMapTrueRdRobin);
//        applicationTestWithAccNoMapTrueRdRobin.setTotalNoOfLines(10000);
//        chunkGeneratorRunnable.generateChunks();
//        ChunkProcessor chunkProcessor = new ChunkProcessor(applicationTestWithAccNoMapTrueRdRobin);
//        chunkProcessor.processChunk(applicationTestWithAccNoMapTrueRdRobin.getChunkDirectoryPath() + "/trade_records_chunk1.csv");
//        TradeProcessor tradeProcessor = new TradeProcessor(QueueDistributor.getTransactionDeque(0), applicationTestWithAccNoMapTrueRdRobin);
//        tradeProcessor.setConnection(connection);
//        tradeProcessor.processTrade("TDB_00000995");
//        String query = "Select positions from positions where account_number='TDB_CUST_5423076' and " +
//                "security_cusip='MSFT'";
//        int positions = 0;
//        ResultSet resultSet = connection.prepareStatement(query).executeQuery();
//        if (resultSet.next()) {
//            positions = resultSet.getInt("positions");
//        }
//        assertEquals(-830, positions);
//    }
//
//    @Test
//    public void testTradeProcessorProcessWithCorrectTradeIdForPositionsEntryUpdate() throws SQLException, InterruptedException, IOException {
//        ChunkGeneratorRunnable chunkGeneratorRunnable = new ChunkGeneratorRunnable(applicationTestWithAccNoMapTrueRdRobin);
//        applicationTestWithAccNoMapTrueRdRobin.setTotalNoOfLines(10000);
//        chunkGeneratorRunnable.generateChunks();
//        ChunkProcessor chunkProcessor = new ChunkProcessor(applicationTestWithAccNoMapTrueRdRobin);
//        chunkProcessor.processChunk(applicationTestWithAccNoMapTrueRdRobin.getChunkDirectoryPath() + "/trade_records_chunk1.csv");
//        TradeProcessor tradeProcessor = new TradeProcessor(QueueDistributor.getTransactionDeque(0), applicationTestWithAccNoMapTrueRdRobin);
//        tradeProcessor.setConnection(connection);
//        tradeProcessor.processTrade("TDB_00000001");
//        tradeProcessor.processTrade("TDB_00000002");
//        String query = "Select version from positions where account_number='TDB_CUST_2517563' and " +
//                "security_cusip='TSLA'";
//        int version = 0;
//        ResultSet resultSet = connection.prepareStatement(query).executeQuery();
//        if (resultSet.next()) {
//            version = resultSet.getInt("version");
//        }
//        assertEquals(2, version);
//    }
//
//    @Test
//    public void testTradeProcessorRetryTransactionDeadLetterQueue() throws InterruptedException {
//        TradeProcessor tradeProcessor = new TradeProcessor(QueueDistributor.getTransactionDeque(0), applicationTestWithAccNoMapTrueRdRobin);
//        tradeProcessor.retryTransaction("TDB_00001000");
//        tradeProcessor.retryTransaction("TDB_00001000");
//        tradeProcessor.retryTransaction("TDB_00001000");
//        tradeProcessor.retryTransaction("TDB_00001000");
//        assertFalse(tradeProcessor.getTradeDeque().isEmpty());
//    }
//
//    @Test
//    public void testTradeProcessorRetryTransactionRetryCountMap() throws InterruptedException {
//        TradeProcessor tradeProcessor = new TradeProcessor(QueueDistributor.getTransactionDeque(0), applicationTestWithAccNoMapTrueRdRobin);
//        tradeProcessor.retryTransaction("TDB_00001000");
//        tradeProcessor.retryTransaction("TDB_00001000");
//        assertEquals(1, tradeProcessor.getRetryCountMap().size());
//    }
//}