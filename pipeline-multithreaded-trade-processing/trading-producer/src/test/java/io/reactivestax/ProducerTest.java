package io.reactivestax;

import io.reactivestax.service.ChunkGeneratorService;
import io.reactivestax.service.ChunkProcessorService;
import io.reactivestax.service.TradeService;
import io.reactivestax.type.exception.InvalidMessagingTechnologyException;
import io.reactivestax.type.exception.InvalidPersistenceTechnologyException;
import io.reactivestax.util.ApplicationPropertiesUtils;
import io.reactivestax.util.QueueProvider;
import io.reactivestax.util.database.TransactionUtil;
import io.reactivestax.util.factory.BeanFactory;
import io.reactivestax.util.messaging.QueueDistributor;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.concurrent.LinkedBlockingQueue;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class ProducerTest {
    ApplicationPropertiesUtils applicationPropertiesUtils;
    TradeService tradeService;
    QueueDistributor queueDistributor;
    ChunkGeneratorService chunkGeneratorService;
    ChunkProcessorService chunkProcessorService;
    TransactionUtil transactionUtil;

    @Before
    public void setUp() {
        applicationPropertiesUtils = ApplicationPropertiesUtils.getInstance("applicationTest.properties");
        applicationPropertiesUtils.loadApplicationProperties("applicationTest.properties");
        queueDistributor = QueueDistributor.getInstance();
        tradeService = TradeService.getInstance();
        chunkGeneratorService = ChunkGeneratorService.getInstance();
        chunkProcessorService = ChunkProcessorService.getInstance();
        transactionUtil = BeanFactory.getTransactionUtil();
    }

    @Test
    public void testGenerateChunks() throws IOException, InterruptedException {
        applicationPropertiesUtils.setTotalNoOfLines(tradeService.fileLineCounter(applicationPropertiesUtils.getFilePath()));
        QueueProvider.getInstance().setChunkQueue(new LinkedBlockingQueue<>(applicationPropertiesUtils.getNumberOfChunks()));
        chunkGeneratorService.generateChunks();
        File directory = new File(applicationPropertiesUtils.getChunkDirectoryPath());
        File[] files = directory.listFiles();
        if (files != null) {
            long fileCount = 0;
            for (File file : files) {
                if (file.isFile()) {
                    fileCount++;
                }
            }
            assertEquals(applicationPropertiesUtils.getNumberOfChunks(), fileCount);
        }
    }

    @Test
    public void testFileLineCounter() throws IOException {
        long counter = tradeService.fileLineCounter(applicationPropertiesUtils.getFilePath());
        Assert.assertEquals(9999, counter);
    }

    @Test
    public void testBuildFilePath() {
        String filePath = tradeService.buildFilePath(5, applicationPropertiesUtils.getChunkFilePathWithName());
        Assert.assertEquals(applicationPropertiesUtils.getChunkFilePathWithName() + 5 + ".csv", filePath);
    }

    @Test
    public void testGetQueueNumberNumberUsingRoundRobinAlgorithm() {
        int queueNumber = queueDistributor.getQueueNumberNumberUsingAlgorithm("round-robin", applicationPropertiesUtils.getTradeProcessorQueueCount());
        assertTrue(queueNumber >= 0 && queueNumber < applicationPropertiesUtils.getTradeProcessorQueueCount());
    }

    @Test
    public void testGetQueueNumberNumberUsingRandomAlgorithm() {
        int queueNumber = queueDistributor.getQueueNumberNumberUsingAlgorithm("random", applicationPropertiesUtils.getTradeProcessorQueueCount());
        assertTrue(queueNumber >= 0 && queueNumber < applicationPropertiesUtils.getTradeProcessorQueueCount());
    }

    @Test
    public void testFigureOutTheNextQueueUsingRoundRobinAlgorithmUseMapTrue() {
        int queueNumber1 = queueDistributor.figureOutTheNextQueue("TDB_00000001", true, "round-robin", applicationPropertiesUtils.getTradeProcessorQueueCount());
        int queueNumber2 = queueDistributor.figureOutTheNextQueue("TDB_00000001", true, "round-robin", applicationPropertiesUtils.getTradeProcessorQueueCount());
        assertEquals(queueNumber1, queueNumber2);
    }

    @Test
    public void testFigureOutTheNextQueueUsingRoundRobinAlgorithmUseMapFalse() {
        int queueNumber = queueDistributor.figureOutTheNextQueue("TDB_00000001", true, "round-robin", applicationPropertiesUtils.getTradeProcessorQueueCount());
        assertTrue(queueNumber >= 0 && queueNumber < applicationPropertiesUtils.getTradeProcessorQueueCount());
    }

    @Test
    public void testFigureOutTheNextQueueUsingRandomAlgorithm() {
        int queueNumber = queueDistributor.figureOutTheNextQueue("TDB_00000001", false, "random",
                applicationPropertiesUtils.getTradeProcessorQueueCount());
        assertTrue(queueNumber >= 0 && queueNumber < applicationPropertiesUtils.getTradeProcessorQueueCount());
    }

    @Test(expected = InvalidPersistenceTechnologyException.class)
    public void testGetTransactionUtilWithInvalidPersistenceTechnology(){
        applicationPropertiesUtils.loadApplicationProperties("applicationTestWithInvalidProperties.properties");
        BeanFactory.getTransactionUtil();
    }

    @Test(expected = InvalidPersistenceTechnologyException.class)
    public void testGetTradePayloadRepositoryWithInvalidPersistenceTechnology(){
        applicationPropertiesUtils.loadApplicationProperties("applicationTestWithInvalidProperties.properties");
        BeanFactory.getTradePayloadRepository();
    }

    @Test(expected = InvalidMessagingTechnologyException.class)
    public void testGetMessageSenderWithInvalidPersistenceTechnology(){
        applicationPropertiesUtils.loadApplicationProperties("applicationTestWithInvalidProperties.properties");
        BeanFactory.getMessageSender();
    }
}
