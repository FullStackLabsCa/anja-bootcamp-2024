package io.reactivestax.service;

import com.rabbitmq.client.*;
import io.reactivestax.database.HibernateUtil;
import io.reactivestax.entity.JournalEntry;
import io.reactivestax.entity.Position;
import io.reactivestax.entity.PositionCompositeKey;
import io.reactivestax.entity.TradePayload;
import io.reactivestax.enums.DirectionEnum;
import io.reactivestax.queueconnection.QueueUtil;
import io.reactivestax.repository.JournalEntryRepository;
import io.reactivestax.repository.PositionsRepository;
import io.reactivestax.repository.SecuritiesReferenceRepository;
import io.reactivestax.repository.TradePayloadRepository;
import io.reactivestax.utility.ApplicationPropertiesUtils;
import jakarta.persistence.OptimisticLockException;
import org.hibernate.HibernateException;
import org.hibernate.Session;

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

public class TradeProcessor implements Callable<Void>, ProcessTrade, ProcessTradeTransaction, RetryTransaction {
    Logger logger = Logger.getLogger(TradeProcessor.class.getName());
    CountDownLatch latch = new CountDownLatch(1);
    String queueName;
    private final Map<String, Integer> retryCountMap;
    ApplicationPropertiesUtils applicationPropertiesUtils;
    Session session;
    int count = 0;
    Channel channel;

    public TradeProcessor(String queueName, ApplicationPropertiesUtils applicationPropertiesUtils) {
        this.queueName = queueName;
        this.retryCountMap = new HashMap<>();
        this.applicationPropertiesUtils = applicationPropertiesUtils;
    }

    public void setRetryCountMap(String key, Integer value) {
        this.retryCountMap.put(key, value);
    }

    @Override
    public Void call() {
        ConnectionFactory connectionFactory = QueueUtil.getInstance(applicationPropertiesUtils).getQueueConnectionFactory();
        try (Session localSession = HibernateUtil.getSessionFactory().openSession();
             Connection connection = connectionFactory.newConnection();
             Channel localChannel = connection.createChannel()) {
            this.channel = localChannel;
            this.session = localSession;
            channel.exchangeDeclare(applicationPropertiesUtils.getQueueExchangeName(), applicationPropertiesUtils.getQueueExchangeType());
            channel.queueDeclare(queueName, true, false, false, null);
            channel.queueBind(queueName, applicationPropertiesUtils.getQueueExchangeName(), queueName);
            DeliverCallback deliverCallback = (consumerTag, delivery) -> {
                String message = new String(delivery.getBody(), StandardCharsets.UTF_8);
                try {
                    processTrade(message);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            };
            CancelCallback cancelCallback = consumerTag -> {
            };
            channel.basicConsume(queueName, true, deliverCallback, cancelCallback);
            latch.await();
        } catch (IOException | TimeoutException | InterruptedException e) {
            logger.warning("Exception detected in Trade Processor.");
            Thread.currentThread().interrupt();
        }

        return null;
    }

    @Override
    public void processTrade(String tradeId) throws InterruptedException, IOException {
        try {
            TradePayloadRepository tradePayloadRepository = new TradePayloadRepository();
            TradePayload tradePayload = tradePayloadRepository.readRawPayload(tradeId, this.session);
            String[] payloadArr = tradePayload.getPayload().split(",");
            String cusip = payloadArr[3];
            SecuritiesReferenceRepository securitiesReferenceRepository = new SecuritiesReferenceRepository();
            boolean validSecurity = securitiesReferenceRepository.lookupSecurities(cusip, this.session);
            tradePayloadRepository.updateTradePayloadLookupStatus(validSecurity, tradePayload.getId(), this.session);
            if (validSecurity) {
                JournalEntry journalEntry = journalEntryTransaction(payloadArr, tradePayload.getId());
                positionTransaction(journalEntry);
            }
        } catch (HibernateException | OptimisticLockException e) {
            logger.warning("Hibernate/Optimistic Lock exception detected.");
            this.session.getTransaction().rollback();
            this.session.clear();
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
        JournalEntryRepository journalEntryRepository = new JournalEntryRepository();
        this.session.beginTransaction();
        journalEntryRepository.insertIntoJournalEntry(journalEntry, this.session);
        TradePayloadRepository tradePayloadRepository = new TradePayloadRepository();
        tradePayloadRepository.updateTradePayloadPostedStatus(tradeId, this.session);
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
        PositionsRepository positionsRepository = new PositionsRepository();
        positionsRepository.upsertPosition(position, this.session);
        JournalEntryRepository journalEntryRepository = new JournalEntryRepository();
        journalEntryRepository.updateJournalEntryStatus(journalEntry.getId(), this.session);
        this.session.getTransaction().commit();
        this.session.clear();
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
