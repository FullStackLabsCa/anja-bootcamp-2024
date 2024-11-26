//package io.reactivestax;
//
//import io.reactivestax.repository.TradePayloadRepository;
//import io.reactivestax.task.ChunkFileProcessor;
//import io.reactivestax.service.ChunkGeneratorService;
//import io.reactivestax.service.ChunkProcessorService;
//import io.reactivestax.service.TradeService;
//import io.reactivestax.type.dto.TradePayload;
//import io.reactivestax.util.ApplicationPropertiesUtils;
//import io.reactivestax.util.QueueProvider;
//import io.reactivestax.util.database.ConnectionUtil;
//import io.reactivestax.util.database.TransactionUtil;
//import io.reactivestax.util.database.hibernate.HibernateTransactionUtil;
//import io.reactivestax.util.factory.BeanFactory;
//import io.reactivestax.util.messaging.rabbitmq.RabbitMQMessageSender;
//import jakarta.persistence.criteria.CriteriaBuilder;
//import jakarta.persistence.criteria.CriteriaDelete;
//import org.hibernate.Session;
//import org.junit.After;
//import org.junit.Assert;
//import org.junit.Before;
//import org.junit.Test;
//import org.mockito.Mock;
//import org.mockito.Mockito;
//import org.mockito.MockitoAnnotations;
//
//import java.io.IOException;
//import java.sql.SQLException;
//import java.util.List;
//import java.util.concurrent.LinkedBlockingQueue;
//import java.util.logging.Logger;
//
//import static org.junit.Assert.assertEquals;
//import static org.mockito.Mockito.*;
//
//public class ProducerHibernateTest {
//    TradePayloadRepository tradePayloadRepository;
//    ConnectionUtil<Session> connectionUtil;
//    TransactionUtil transactionUtil;
//    ApplicationPropertiesUtils applicationPropertiesUtils;
//    TradeService tradeService;
//    ChunkGeneratorService chunkGeneratorService;
//    ChunkProcessorService chunkProcessorService;
//    Logger logger = Logger.getLogger(ProducerHibernateTest.class.getName());
//    @Mock
//    RabbitMQMessageSender rabbitMQMessageSender;
//
//    @Before
//    public void setUp() {
//        MockitoAnnotations.openMocks(this);
//        applicationPropertiesUtils = ApplicationPropertiesUtils.getInstance("applicationHibernateTest.properties");
//        applicationPropertiesUtils.loadApplicationProperties("applicationHibernateTest.properties");
//        connectionUtil = HibernateTransactionUtil.getInstance();
//        tradePayloadRepository = BeanFactory.getTradePayloadRepository();
//        transactionUtil = BeanFactory.getTransactionUtil();
//        tradeService = TradeService.getInstance();
//        chunkGeneratorService = ChunkGeneratorService.getInstance();
//        chunkProcessorService = ChunkProcessorService.getInstance();
//    }
//
//    @After
//    public void cleanUp() {
//        Session session = connectionUtil.getConnection();
//        session.beginTransaction();
//        CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
//        CriteriaDelete<io.reactivestax.repository.hibernate.entity.TradePayload> criteriaDelete =
//                criteriaBuilder.createCriteriaDelete(io.reactivestax.repository.hibernate.entity.TradePayload.class);
//        session.createMutationQuery(criteriaDelete).executeUpdate();
//        session.getTransaction().commit();
//    }
//
//    @Test
//    public void testInsertRawPayloadWithTwoDifferentRecords() {
//        transactionUtil.startTransaction();
//        TradePayload tradePayload1 = new TradePayload();
//        tradePayload1.setPayload("TDB_00000001,2024-09-25 06:58:37,TDB_CUST_2517563,TSLA,SELL,45,1480.82");
//        tradePayload1.setTradeNumber("TDB_00000001");
//        tradePayload1.setValidityStatus("VALID");
//        tradePayloadRepository.insertTradeRawPayload(tradePayload1);
//        transactionUtil.commitTransaction();
//        transactionUtil.startTransaction();
//        TradePayload tradePayload2 = new TradePayload();
//        tradePayload2.setPayload("TDB_00000002,2024-09-25 06:58:37,TDB_CUST_2517563,V,SELL,45,1480.82");
//        tradePayload2.setTradeNumber("TDB_00000002");
//        tradePayload2.setValidityStatus("INVALID");
//        tradePayloadRepository.insertTradeRawPayload(tradePayload2);
//        transactionUtil.commitTransaction();
//        Session session = connectionUtil.getConnection();
//        List<io.reactivestax.repository.hibernate.entity.TradePayload> tradePayloadList = session.createQuery("from " +
//                        "TradePayload",
//                io.reactivestax.repository.hibernate.entity.TradePayload.class).getResultList();
//        Assert.assertEquals(2, tradePayloadList.size());
//    }
//
//    @Test
//    public void testInsertRawPayloadWithTwoSameRecords() {
//        TradePayload tradePayload1 = new TradePayload();
//        tradePayload1.setPayload("TDB_00000001,2024-09-25 06:58:37,TDB_CUST_2517563,TSLA,SELL,45,1480.82");
//        tradePayload1.setTradeNumber("TDB_00000001");
//        tradePayload1.setValidityStatus("VALID");
//        transactionUtil.startTransaction();
//        tradePayloadRepository.insertTradeRawPayload(tradePayload1);
//        transactionUtil.commitTransaction();
//        transactionUtil.startTransaction();
//        TradePayload tradePayload2 = new TradePayload();
//        tradePayload2.setPayload("TDB_00000001,2024-09-25 06:58:37,TDB_CUST_2517563,TSLA,SELL,45,1480.82");
//        tradePayload2.setTradeNumber("TDB_00000001");
//        tradePayload2.setValidityStatus("VALID");
//        try {
//            tradePayloadRepository.insertTradeRawPayload(tradePayload2);
//        } catch (Exception e) {
//            logger.warning("Insertion failed.");
//        }
//        transactionUtil.commitTransaction();
//        Session session = connectionUtil.getConnection();
//        List<io.reactivestax.repository.hibernate.entity.TradePayload> tradePayloadList = session.createQuery("from " +
//                        "TradePayload",
//                io.reactivestax.repository.hibernate.entity.TradePayload.class).getResultList();
//        Assert.assertEquals(1, tradePayloadList.size());
//    }
//
//    @Test
//    public void testProcessChunk() throws IOException, InterruptedException, SQLException {
//        applicationPropertiesUtils.setTotalNoOfLines(tradeService.fileLineCounter(applicationPropertiesUtils.getFilePath()));
//        QueueProvider.getInstance().setChunkQueue(new LinkedBlockingQueue<>(applicationPropertiesUtils.getNumberOfChunks()));
//        chunkGeneratorService.generateChunks();
//        String chunkFilePath = tradeService.buildFilePath(1, applicationPropertiesUtils.getChunkFilePathWithName());
//        long lineCount = tradeService.fileLineCounter(chunkFilePath) + 1;
//        chunkProcessorService.processChunk(chunkFilePath);
//        transactionUtil.startTransaction();
//        Session session = connectionUtil.getConnection();
//        List<io.reactivestax.repository.hibernate.entity.TradePayload> tradePayloads = session.createQuery("from TradePayload", io.reactivestax.repository.hibernate.entity.TradePayload.class).getResultList();
//        transactionUtil.rollbackTransaction();
//        assertEquals(lineCount, tradePayloads.size());
//    }
//
//    @Test
//    public void testProcessChunkWithInvalidFilePath() throws SQLException {
//        transactionUtil.startTransaction();
//        chunkProcessorService.processChunk("wrongFilePath");
//        transactionUtil.startTransaction();
//        Session session = connectionUtil.getConnection();
//        List<io.reactivestax.repository.hibernate.entity.TradePayload> tradePayloads = session.createQuery("from TradePayload", io.reactivestax.repository.hibernate.entity.TradePayload.class).getResultList();
//        transactionUtil.rollbackTransaction();
//        assertEquals(0, tradePayloads.size());
//    }
//
//    @Test
//    public void testChunkProcessorRun() throws IOException, InterruptedException {
//        doNothing().when(rabbitMQMessageSender).sendMessage(Mockito.anyString(), Mockito.anyString());
//        applicationPropertiesUtils.setTotalNoOfLines(tradeService.fileLineCounter(applicationPropertiesUtils.getFilePath()));
//        QueueProvider.getInstance().setChunkQueue(new LinkedBlockingQueue<>(applicationPropertiesUtils.getNumberOfChunks()));
//        chunkGeneratorService.generateChunks();
//        String chunkFilePath = tradeService.buildFilePath(1, applicationPropertiesUtils.getChunkFilePathWithName());
//        long lineCount = tradeService.fileLineCounter(chunkFilePath) + 1;
//        ChunkFileProcessor chunkFileProcessor = new ChunkFileProcessor();
//        chunkFileProcessor.run();
//        verify(rabbitMQMessageSender, times(1)).sendMessage(Mockito.anyString(), Mockito.anyString());
//        transactionUtil.startTransaction();
//        Session session = connectionUtil.getConnection();
//        List<io.reactivestax.repository.hibernate.entity.TradePayload> tradePayloads = session.createQuery("from TradePayload", io.reactivestax.repository.hibernate.entity.TradePayload.class).getResultList();
//        transactionUtil.rollbackTransaction();
//        assertEquals(lineCount, tradePayloads.size());
//    }
//}
