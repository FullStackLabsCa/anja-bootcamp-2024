package io.reactivestax.service;

import io.reactivestax.repository.JournalEntryRepository;
import io.reactivestax.repository.LookupSecuritiesRepository;
import io.reactivestax.repository.PositionsRepository;
import io.reactivestax.repository.TradePayloadRepository;
import io.reactivestax.task.TradeProcessor;
import io.reactivestax.type.dto.JournalEntry;
import io.reactivestax.type.dto.Position;
import io.reactivestax.type.dto.TradePayload;
import io.reactivestax.type.enums.Direction;
import io.reactivestax.type.exception.OptimisticLockingException;
import io.reactivestax.util.database.TransactionUtil;
import io.reactivestax.util.factory.BeanFactory;
import jakarta.persistence.OptimisticLockException;
import org.hibernate.HibernateException;

import java.io.IOException;
import java.util.logging.Logger;

public class TradeProcessorService implements TradeProcessor {
    private static TradeProcessorService instance;
    Logger logger = Logger.getLogger(TradeProcessorService.class.getName());
    private final TransactionUtil transactionUtil;
    private final TradePayloadRepository tradePayloadRepository;
    private final LookupSecuritiesRepository lookupSecuritiesRepository;
    private final JournalEntryRepository journalEntryRepository;
    private final PositionsRepository positionsRepository;

    private TradeProcessorService() {
        transactionUtil = BeanFactory.getTransactionUtil();
        tradePayloadRepository = BeanFactory.getTradePayloadRepository();
        lookupSecuritiesRepository = BeanFactory.getLookupSecuritiesRepository();
        journalEntryRepository = BeanFactory.getJournalEntryRepository();
        positionsRepository = BeanFactory.getPositionsRepository();
    }

    public static synchronized TradeProcessorService getInstance() {
        if (instance == null) {
            instance = new TradeProcessorService();
        }

        return instance;
    }

    @Override
    public void processTrade(String tradeId, String queueName) throws InterruptedException, IOException {
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
            logger.warning("Hibernate/SQL/Optimistic Lock exception detected.");
            transactionUtil.rollbackTransaction();
            BeanFactory.getTransactionRetryer().retryTransaction(tradeId, queueName);
        }
    }

    private JournalEntry journalEntryTransaction(String[] payloadArr, Long tradeId) {
        JournalEntry journalEntry = new JournalEntry();
        journalEntry.setTradeId(payloadArr[0]);
        journalEntry.setAccountNumber(payloadArr[2]);
        journalEntry.setSecurityCusip(payloadArr[3]);
        journalEntry.setDirection(payloadArr[4]);
        journalEntry.setQuantity(Integer.parseInt(payloadArr[5]));
        journalEntry.setTransactionTimestamp(payloadArr[1]);
        journalEntryRepository.insertIntoJournalEntry(journalEntry).ifPresent(journalEntry::setId);
        tradePayloadRepository.updateTradePayloadPostedStatus(tradeId);
        return journalEntry;
    }

    private void positionTransaction(JournalEntry journalEntry) {
        Position position = new Position();
        position.setAccountNumber(journalEntry.getAccountNumber());
        position.setSecurityCusip(journalEntry.getSecurityCusip());
        position.setHolding((long) (journalEntry.getDirection().equals(Direction.SELL.toString()) ? -journalEntry.getQuantity() : journalEntry.getQuantity()));
        positionsRepository.upsertPosition(position);
        journalEntryRepository.updateJournalEntryStatus(journalEntry.getId());
    }
}
