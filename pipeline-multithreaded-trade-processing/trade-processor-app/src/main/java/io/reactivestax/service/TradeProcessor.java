package io.reactivestax.service;

import io.reactivestax.database.HibernateUtil;
import io.reactivestax.entity.JournalEntry;
import io.reactivestax.entity.Position;
import io.reactivestax.entity.PositionCompositeKey;
import io.reactivestax.entity.TradePayload;
import io.reactivestax.enums.DirectionEnum;
import io.reactivestax.repository.JournalEntryRepository;
import io.reactivestax.repository.PositionsRepository;
import io.reactivestax.repository.SecuritiesReferenceRepository;
import io.reactivestax.repository.TradePayloadRepository;
import io.reactivestax.utility.ApplicationPropertiesUtils;
import jakarta.persistence.OptimisticLockException;
import org.hibernate.HibernateException;
import org.hibernate.Session;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

public class TradeProcessor implements Runnable, ProcessTrade, ProcessTradeTransaction, RetryTransaction {
    Logger logger = Logger.getLogger(TradeProcessor.class.getName());
    LinkedBlockingDeque<String> tradeDeque;
    private final Map<String, Integer> retryCountMap;
    ApplicationPropertiesUtils applicationPropertiesUtils;
    Session session;

    public TradeProcessor(LinkedBlockingDeque<String> tradeDeque, ApplicationPropertiesUtils applicationPropertiesUtils) {
        this.tradeDeque = tradeDeque;
        this.retryCountMap = new HashMap<>();
        this.applicationPropertiesUtils = applicationPropertiesUtils;
    }

    public void setRetryCountMap(String key, Integer value) {
        this.retryCountMap.put(key, value);
    }

    @Override
    public void run() {
        String tradeId = "";
        try (Session localSession = HibernateUtil.getSessionFactory().openSession()) {
            this.session = localSession;
            while (true) {
                tradeId = this.tradeDeque.poll(500, TimeUnit.MILLISECONDS);
                if (tradeId == null) break;
                else processTrade(tradeId);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            logger.warning("Thread was interrupted.");
        } catch (HibernateException e) {
            logger.warning("Hibernate exception detected.");
        }
    }

    @Override
    public void processTrade(String tradeId) throws InterruptedException {
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
    public void retryTransaction(String tradeId) throws InterruptedException {
        int retryCount = this.retryCountMap.getOrDefault(tradeId, 0) + 1;
        if (retryCount >= this.applicationPropertiesUtils.getMaxRetryCount()) {
            QueueDistributor.deadLetterTransactionDeque.putLast(tradeId);
            this.retryCountMap.remove(tradeId);
        } else {
            this.tradeDeque.putFirst(tradeId);
            setRetryCountMap(tradeId, retryCount);
        }
    }
}
