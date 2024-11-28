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
//}
