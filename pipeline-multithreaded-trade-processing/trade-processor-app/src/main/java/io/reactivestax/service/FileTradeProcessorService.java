package io.reactivestax.service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeoutException;
import java.util.logging.Logger;

import org.hibernate.HibernateException;
import org.hibernate.Session;

import com.rabbitmq.client.CancelCallback;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;

import io.reactivestax.entity.JournalEntry;
import io.reactivestax.entity.Position;
import io.reactivestax.entity.PositionCompositeKey;
import io.reactivestax.entity.TradePayload;
import io.reactivestax.enums.DirectionEnum;
import io.reactivestax.utility.rabbitmq.QueueUtil;
import io.reactivestax.repository.hibernate.HibernateJournalEntryRepository;
import io.reactivestax.repository.hibernate.HibernatePositionsRepository;
import io.reactivestax.repository.hibernate.HibernateSecuritiesReferenceRepository;
import io.reactivestax.repository.hibernate.HibernateTradePayloadRepository;
import io.reactivestax.utility.ApplicationPropertiesUtils;
import jakarta.persistence.OptimisticLockException;

public class FileTradeProcessorService implements Callable<Void>, TradeProcessorService, RetryTransaction {
    Logger logger = Logger.getLogger(FileTradeProcessorService.class.getName());
    CountDownLatch latch = new CountDownLatch(1);
    String queueName;
    private final Map<String, Integer> retryCountMap;
    ApplicationPropertiesUtils applicationPropertiesUtils;
    Session session;
    int count = 0;
    Channel channel;

    public FileTradeProcessorService(String queueName, ApplicationPropertiesUtils applicationPropertiesUtils) {
        this.queueName = queueName;
        this.retryCountMap = new HashMap<>();
        this.applicationPropertiesUtils = applicationPropertiesUtils;
    }

    public void setRetryCountMap(String key, Integer value) {
        this.retryCountMap.put(key, value);
    }

    @Override
    public Void call() {
//        ConnectionFactory connectionFactory = QueueUtil.getInstance(applicationPropertiesUtils).getQueueConnectionFactory();
//        try (
//              /*  Session localSession = HibernateConnectionUtil.getSessionFactory().openSession();*/
//             Connection connection = connectionFactory.newConnection();
//             Channel localChannel = connection.createChannel()) {
//            this.channel = localChannel;
//            this.session = localSession;
//            channel.exchangeDeclare(applicationPropertiesUtils.getQueueExchangeName(), applicationPropertiesUtils.getQueueExchangeType());
//            channel.queueDeclare(queueName, true, false, false, null);
//            channel.queueBind(queueName, applicationPropertiesUtils.getQueueExchangeName(), queueName);
//            DeliverCallback deliverCallback = (consumerTag, delivery) -> {
//                String message = new String(delivery.getBody(), StandardCharsets.UTF_8);
//                try {
//                    processTrade(message);
//                } catch (InterruptedException e) {
//                    Thread.currentThread().interrupt();
//                }
//            };
//            CancelCallback cancelCallback = consumerTag -> {
//            };
//            channel.basicConsume(queueName, true, deliverCallback, cancelCallback);
//            latch.await();
//        } catch (IOException | TimeoutException | InterruptedException e) {
//            logger.warning("Exception detected in Trade Processor.");
//            Thread.currentThread().interrupt();
//        }

        return null;
    }

    @Override
    
    public void processTrade(String tradeId) throws InterruptedException, IOException {
        try {
//            HibernateTradePayloadRepository hibernateTradePayloadRepository = new HibernateTradePayloadRepository();
////            ServiceUtil.beginTransaction();
//            this.session.beginTransaction();
//            TradePayload tradePayload = hibernateTradePayloadRepository.readRawPayload(tradeId);
//            String[] payloadArr = tradePayload.getPayload().split(",");
//            String cusip = payloadArr[3];
//            HibernateSecuritiesReferenceRepository hibernateSecuritiesReferenceRepository = new HibernateSecuritiesReferenceRepository();
//            boolean validSecurity = hibernateSecuritiesReferenceRepository.lookupSecurities(cusip);
//            hibernateTradePayloadRepository.updateTradePayloadLookupStatus(validSecurity, tradePayload.getId());
//            if (validSecurity) {
//                JournalEntry journalEntry = journalEntryTransaction(payloadArr, tradePayload.getId());
//                positionTransaction(journalEntry);
//            }
//            this.session.getTransaction().commit();
//            ServiceUtil.commitTransaction();
//            this.session.clear();
        } catch (HibernateException | OptimisticLockException e) {
            logger.warning("Hibernate/Optimistic Lock exception detected.");
            //this.session.getTransaction().rollback();
//            ServiceUtil.rollbackTransaction();
            //this.session.clear();
            retryTransaction(tradeId);
        }
    }

    @Override
    public JournalEntry journalEntryTransaction(String[] payloadArr, int tradeId) {
        JournalEntry journalEntry = new JournalEntry();
        journalEntry.setTradeId(payloadArr[0]);
        journalEntry.setAccountNumber(payloadArr[2]);
        journalEntry.setSecurityCusip(payloadArr[3]);
        journalEntry.setDirection(DirectionEnum.valueOf(payloadArr[4]));
        journalEntry.setQuantity(Integer.parseInt(payloadArr[5]));
        journalEntry.setTransactionDateTime(Timestamp.valueOf(LocalDateTime.parse(payloadArr[1], DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))));
        HibernateJournalEntryRepository hibernateJournalEntryRepository = new HibernateJournalEntryRepository();
        hibernateJournalEntryRepository.insertIntoJournalEntry(journalEntry, this.session);
        //
        HibernateTradePayloadRepository hibernateTradePayloadRepository = HibernateTradePayloadRepository.getInstance();
        hibernateTradePayloadRepository.updateTradePayloadPostedStatus(tradeId);
        return journalEntry;
    }

    @Override
    public void positionTransaction(JournalEntry journalEntry) {
        Position position = new Position();
        PositionCompositeKey positionCompositeKey = new PositionCompositeKey();
        positionCompositeKey.setAccountNumber(journalEntry.getAccountNumber());
        positionCompositeKey.setSecurityCusip(journalEntry.getSecurityCusip());
        position.setPositionCompositeKey(positionCompositeKey);
        position.setHolding(journalEntry.getDirection().equals(DirectionEnum.SELL) ? -journalEntry.getQuantity() :
                journalEntry.getQuantity());
        HibernatePositionsRepository hibernatePositionsRepository = new HibernatePositionsRepository();
        hibernatePositionsRepository.upsertPosition(position, this.session);
        HibernateJournalEntryRepository hibernateJournalEntryRepository = new HibernateJournalEntryRepository();
        hibernateJournalEntryRepository.updateJournalEntryStatus(journalEntry.getId(), this.session);
    }

    @Override
    public void retryTransaction(String tradeId) throws InterruptedException, IOException {
        int retryCount = this.retryCountMap.getOrDefault(tradeId, 0) + 1;
        if (retryCount >= this.applicationPropertiesUtils.getMaxRetryCount()) {
            QueueDistributor.deadLetterTransactionDeque.putLast(tradeId);
            this.retryCountMap.remove(tradeId);
        } else {
            channel.basicPublish(applicationPropertiesUtils.getQueueExchangeName(), this.queueName, null,
                    tradeId.getBytes(StandardCharsets.UTF_8));
            setRetryCountMap(tradeId, retryCount);
        }
    }
}
