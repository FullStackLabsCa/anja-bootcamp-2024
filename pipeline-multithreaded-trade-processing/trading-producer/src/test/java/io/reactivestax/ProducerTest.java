//package io.reactivestax;
//
//import io.reactivestax.task.ChunkFileGenerator;
//import io.reactivestax.service.ChunkGeneratorService;
//import io.reactivestax.service.ChunkProcessorService;
//import io.reactivestax.service.TradeService;
//import io.reactivestax.type.exception.InvalidMessagingTechnologyException;
//import io.reactivestax.type.exception.InvalidPersistenceTechnologyException;
//import io.reactivestax.util.ApplicationPropertiesUtils;
//import io.reactivestax.util.QueueProvider;
//import io.reactivestax.util.database.TransactionUtil;
//import io.reactivestax.util.messaging.QueueDistributor;
//import org.junit.Assert;
//import org.junit.Before;
//import org.junit.Test;
//
//import java.io.File;
//import java.io.IOException;
//import java.util.concurrent.LinkedBlockingQueue;
//
//import static org.junit.Assert.assertEquals;
//import static org.junit.Assert.assertTrue;
//
//public class ProducerTest {
//    ApplicationPropertiesUtils applicationPropertiesUtils;
//    TradeService tradeService;
//    QueueDistributor queueDistributor;
//    ChunkGeneratorService chunkGeneratorService;
//    ChunkProcessorService chunkProcessorService;
//    TransactionUtil transactionUtil;
//
//    @Before
//    public void setUp() {
//        applicationPropertiesUtils = ApplicationPropertiesUtils.getInstance("applicationTest.properties");
//        applicationPropertiesUtils.loadApplicationProperties("applicationTest.properties");
//        queueDistributor = QueueDistributor.getInstance();
//        tradeService = TradeService.getInstance();
//        chunkGeneratorService = ChunkGeneratorService.getInstance();
//        chunkProcessorService = ChunkProcessorService.getInstance();
//        transactionUtil = BeanFactory.getTransactionUtil();
//    }
//
//    @Test
//    public void testFileLineCounter() throws IOException {
//        long counter = tradeService.fileLineCounter(applicationPropertiesUtils.getFilePath());
//        Assert.assertEquals(9999, counter);
//    }
//
//    @Test
//    public void testBuildFilePath() {
//        String filePath = tradeService.buildFilePath(5, applicationPropertiesUtils.getChunkFilePathWithName());
//        Assert.assertEquals(applicationPropertiesUtils.getChunkFilePathWithName() + 5 + ".csv", filePath);
//    }
//
//
//
//
//    @Test
//    public void testChunkFileGeneratorRun() throws IOException {
//        applicationPropertiesUtils.setTotalNoOfLines(tradeService.fileLineCounter(applicationPropertiesUtils.getFilePath()));
//        ChunkFileGenerator chunkFileGenerator = new ChunkFileGenerator();
//        chunkFileGenerator.run();
//        long fileCount = 0;
//        File directory = new File(applicationPropertiesUtils.getChunkDirectoryPath());
//        File[] files = directory.listFiles();
//        if (files != null) {
//            for (File file : files) {
//                if (file.isFile()) {
//                    fileCount++;
//                }
//            }
//        }
//        assertEquals(applicationPropertiesUtils.getNumberOfChunks(), fileCount);
//    }
//}
