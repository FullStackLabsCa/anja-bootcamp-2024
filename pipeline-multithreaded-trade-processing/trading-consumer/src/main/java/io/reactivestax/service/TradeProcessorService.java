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
import java.util.Optional;
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

            Optional<TradePayload> optionalTradePayload = tradePayloadRepository.readRawPayload(tradeId);
            optionalTradePayload.ifPresent(
                    tradePayload -> processTradePayload(tradePayload, tradeId, queueName)
            );

            transactionUtil.commitTransaction();
        } catch (HibernateException | OptimisticLockException | OptimisticLockingException e) {
            logger.warning("Hibernate/Optimistic Lock exception detected.");
            transactionUtil.rollbackTransaction();
            BeanFactory.getTransactionRetryer().retryTransaction(tradeId, queueName);
        }
    }

    private void processTradePayload(TradePayload tradePayload, String tradeId, String queueName) {
        String[] payloadArr = tradePayload.getPayload().split(",");
        String cusip = payloadArr[3];
        boolean validSecurity = lookupSecuritiesRepository.lookupSecurities(cusip);
        tradePayloadRepository.updateTradePayloadLookupStatus(validSecurity, tradePayload.getId());
        if (validSecurity) {
            JournalEntry journalEntry = journalEntryTransaction(payloadArr, tradePayload.getId());
            positionTransaction(journalEntry);
        }
    }

    @Override
    public JournalEntry journalEntryTransaction(String[] payloadArr, Long tradeId) {
        JournalEntry journalEntry = JournalEntry.builder()
                .tradeId(payloadArr[0])
                .accountNumber(payloadArr[2])
                .securityCusip(payloadArr[3])
                .direction(payloadArr[4])
                .quantity(Integer.parseInt(payloadArr[5]))
                .transactionTimestamp(payloadArr[1])
                .build();

        Optional<Long> optionalJournalEntryId = journalEntryRepository.insertIntoJournalEntry(journalEntry);
        optionalJournalEntryId
                .ifPresent(journalEntry::setId);
        tradePayloadRepository.updateTradePayloadPostedStatus(tradeId);
        return journalEntry;
    }

    @Override
    public void positionTransaction(JournalEntry journalEntry) {
        Position position = new Position();
        position.setAccountNumber(journalEntry.getAccountNumber());
        position.setSecurityCusip(journalEntry.getSecurityCusip());
        position.setHolding((long) (journalEntry.getDirection().equals(Direction.SELL.toString()) ? -journalEntry.getQuantity() : journalEntry.getQuantity()));
        positionsRepository.upsertPosition(position);
        journalEntryRepository.updateJournalEntryStatus(journalEntry.getId());
    }
}
