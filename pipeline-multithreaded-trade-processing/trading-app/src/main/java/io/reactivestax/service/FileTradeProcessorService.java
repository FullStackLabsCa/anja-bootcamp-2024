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

import com.rabbitmq.client.*;
import org.hibernate.HibernateException;

import io.reactivestax.entity.JournalEntry;
import io.reactivestax.entity.Position;
import io.reactivestax.entity.PositionCompositeKey;
import io.reactivestax.entity.TradePayload;
import io.reactivestax.enums.DirectionEnum;
import io.reactivestax.exceptions.OptimisticLockingException;
import io.reactivestax.factory.BeanFactory;
import io.reactivestax.repository.JournalEntryRepository;
import io.reactivestax.repository.LookupSecuritiesRepository;
import io.reactivestax.repository.PositionsRepository;
import io.reactivestax.repository.TradePayloadRepository;
import io.reactivestax.utility.ApplicationPropertiesUtils;
import io.reactivestax.utility.database.TransactionUtil;
import io.reactivestax.utility.messaging.TransactionRetryer;
import io.reactivestax.utility.messaging.rabbitmq.RabbitMQMessageReceiver;
import jakarta.persistence.OptimisticLockException;

public class FileTradeProcessorService implements Callable<Void>, TradeProcessorService, TransactionRetryer {
    Logger logger = Logger.getLogger(FileTradeProcessorService.class.getName());
    CountDownLatch latch = new CountDownLatch(1);
    String queueName;
    private final TradePayloadRepository tradePayloadRepository;
    private final TransactionUtil transactionUtil;
    private final LookupSecuritiesRepository lookupSecuritiesRepository;
    private final JournalEntryRepository journalEntryRepository;
    private final PositionsRepository positionsRepository;
    Channel channel;
    Delivery deliveryGlobal;

    public FileTradeProcessorService(String queueName) {
        this.queueName = queueName;
        this.transactionUtil = BeanFactory.getTransactionUtil();
        this.tradePayloadRepository = BeanFactory.getTradePayloadRepository();
        this.lookupSecuritiesRepository = BeanFactory.getLookupSecuritiesRepository();
        this.journalEntryRepository = BeanFactory.getJournalEntryRepository();
        this.positionsRepository = BeanFactory.getPositionsRepository();
    }

    @Override
    public Void call() {
        try {
            RabbitMQMessageReceiver rabbitMQMessageReceiver = new RabbitMQMessageReceiver();
            this.channel = rabbitMQMessageReceiver.getReceiverChannel(queueName);
            DeliverCallback deliverCallback = (consumerTag, delivery) -> {
                deliveryGlobal = delivery;
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
            e.printStackTrace();
            logger.warning("Exception detected in Trade Processor.");
            Thread.currentThread().interrupt();
        }

        return null;
    }

    @Override

    public void processTrade(String tradeId) throws InterruptedException, IOException {
        try {
            transactionUtil.startTransaction();
            TradePayload tradePayload = tradePayloadRepository.readRawPayload(tradeId);
            String[] payloadArr = tradePayload.getPayload().split(",");
            String cusip = payloadArr[3];
            boolean validSecurity = lookupSecuritiesRepository.lookupSecurities(cusip);
            tradePayloadRepository.updateTradePayloadLookupStatus(validSecurity, tradePayload.getId());
            if (validSecurity) {
                JournalEntry journalEntry = journalEntryTransaction(payloadArr, tradePayload.getId());
                positionTransaction(journalEntry);
            }
            transactionUtil.commitTransaction();
        } catch (HibernateException | OptimisticLockException | OptimisticLockingException e) {
            logger.warning("Hibernate/Optimistic Lock exception detected.");
            transactionUtil.rollbackTransaction();
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
        journalEntryRepository.insertIntoJournalEntry(journalEntry);
        tradePayloadRepository.updateTradePayloadPostedStatus(tradeId);
        return journalEntry;
    }

    @Override
    public void positionTransaction(JournalEntry journalEntry) {
        Position position = new Position();
        PositionCompositeKey positionCompositeKey = new PositionCompositeKey();
        positionCompositeKey.setAccountNumber(journalEntry.getAccountNumber());
        positionCompositeKey.setSecurityCusip(journalEntry.getSecurityCusip());
        position.setPositionCompositeKey(positionCompositeKey);
        position.setHolding(journalEntry.getDirection().equals(DirectionEnum.SELL) ? -journalEntry.getQuantity() : journalEntry.getQuantity());
        positionsRepository.upsertPosition(position);
        journalEntryRepository.updateJournalEntryStatus(journalEntry.getId());
    }

    @Override
    public void retryTransaction(String tradeId) throws IOException {
        String retryHeader = "x-retry-count";
        Map<String, Object> headers = deliveryGlobal.getProperties().getHeaders();
        int retryCount = (headers != null && headers.containsKey(retryHeader)) ? (int) headers.get(retryHeader) : 0;
        ApplicationPropertiesUtils applicationPropertiesUtils = ApplicationPropertiesUtils.getInstance();
        if (retryCount < applicationPropertiesUtils.getMaxRetryCount()) {
            Map<String, Object> retryHeaders = new HashMap<>();
            retryHeaders.put(retryHeader, retryCount + 1);
            AMQP.BasicProperties retryProps = new AMQP.BasicProperties.Builder()
                    .headers(retryHeaders)
                    .build();
            System.out.println("Retrying message to retryQueue, attempt: " + (retryCount + 1));
            channel.basicPublish(applicationPropertiesUtils.getQueueExchangeName(),
                    applicationPropertiesUtils.getRetryQueueName() + queueName.substring(queueName.length() - 2),
                    retryProps,
                    tradeId.getBytes(StandardCharsets.UTF_8));

        } else {
            System.out.println("Sending message to DLQ after " + applicationPropertiesUtils.getMaxRetryCount() + " retries.");
            channel.basicPublish("", applicationPropertiesUtils.getDlqName(), null, tradeId.getBytes());
        }
    }
}
