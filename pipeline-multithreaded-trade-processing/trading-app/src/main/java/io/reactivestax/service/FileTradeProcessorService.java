package io.reactivestax.service;

import io.reactivestax.entity.JournalEntry;
import io.reactivestax.entity.Position;
import io.reactivestax.entity.PositionCompositeKey;
import io.reactivestax.entity.TradePayload;
import io.reactivestax.enums.Direction;
import io.reactivestax.exception.OptimisticLockingException;
import io.reactivestax.factory.BeanFactory;
import io.reactivestax.repository.JournalEntryRepository;
import io.reactivestax.repository.LookupSecuritiesRepository;
import io.reactivestax.repository.PositionsRepository;
import io.reactivestax.repository.TradePayloadRepository;
import io.reactivestax.util.database.TransactionUtil;
import io.reactivestax.util.messaging.MessageReceiver;
import io.reactivestax.util.messaging.TransactionRetryer;
import jakarta.persistence.OptimisticLockException;
import org.hibernate.HibernateException;

import java.io.IOException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.logging.Logger;

public class FileTradeProcessorService implements Callable<Void>, TradeProcessorService {
    Logger logger = Logger.getLogger(FileTradeProcessorService.class.getName());
    String queueName;
    private final TradePayloadRepository tradePayloadRepository;
    private final TransactionUtil transactionUtil;
    private final LookupSecuritiesRepository lookupSecuritiesRepository;
    private final JournalEntryRepository journalEntryRepository;
    private final PositionsRepository positionsRepository;
    private final TransactionRetryer transactionRetryer;
    private final CountDownLatch latch = new CountDownLatch(1);

    int count = 0;

    public FileTradeProcessorService(String queueName) {
        this.queueName = queueName;
        this.transactionUtil = BeanFactory.getTransactionUtil();
        this.tradePayloadRepository = BeanFactory.getTradePayloadRepository();
        this.lookupSecuritiesRepository = BeanFactory.getLookupSecuritiesRepository();
        this.journalEntryRepository = BeanFactory.getJournalEntryRepository();
        this.positionsRepository = BeanFactory.getPositionsRepository();
        this.transactionRetryer = BeanFactory.getTransactionRetryer();
    }

    @Override
    public Void call() throws InterruptedException {
        try {
            MessageReceiver messageReceiver = BeanFactory.getMessageReceiver();
            while (count == 0) {
                String tradeId = messageReceiver.receiveMessage(queueName);
                if (!tradeId.isEmpty()) {
                    processTrade(tradeId);
                } else {
                    logger.info("No trade ID received. Waiting for messages...");
                }
            }
        } catch (IOException | InterruptedException e) {
            logger.warning("Exception detected in Trade Processor.");
            Thread.currentThread().interrupt();
        }
        latch.await();
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
            transactionRetryer.retryTransaction(tradeId, queueName);
        }
    }

    @Override
    public JournalEntry journalEntryTransaction(String[] payloadArr, Long tradeId) {
        JournalEntry journalEntry = new JournalEntry();
        journalEntry.setTradeId(payloadArr[0]);
        journalEntry.setAccountNumber(payloadArr[2]);
        journalEntry.setSecurityCusip(payloadArr[3]);
        journalEntry.setDirection(Direction.valueOf(payloadArr[4]));
        journalEntry.setQuantity(Integer.parseInt(payloadArr[5]));
        journalEntry.setTransactionTimestamp(Timestamp.valueOf(LocalDateTime.parse(payloadArr[1], DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))));
        Long journalEntryId = journalEntryRepository.insertIntoJournalEntry(journalEntry);
        if(journalEntryId != null) journalEntry.setId(journalEntryId);
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
        position.setHolding((long) (journalEntry.getDirection().equals(Direction.SELL) ? -journalEntry.getQuantity() : journalEntry.getQuantity()));
        positionsRepository.upsertPosition(position);
        journalEntryRepository.updateJournalEntryStatus(journalEntry.getId());
    }
}
