package io.reactivestax.service;

import java.io.IOException;
import java.util.Optional;
import java.util.logging.Logger;

import org.hibernate.HibernateException;

import io.reactivestax.repository.JournalEntryRepository;
import io.reactivestax.repository.LookupSecuritiesRepository;
import io.reactivestax.repository.PositionsRepository;
import io.reactivestax.repository.TradePayloadRepository;
import io.reactivestax.task.TradeProcessor;
import io.reactivestax.type.dto.JournalEntryDTO;
import io.reactivestax.type.dto.PositionDTO;
import io.reactivestax.type.dto.TradePayloadDTO;
import io.reactivestax.type.enums.Direction;
import io.reactivestax.type.exception.OptimisticLockingException;
import io.reactivestax.util.database.TransactionUtil;
import io.reactivestax.util.factory.BeanFactory;
import jakarta.persistence.OptimisticLockException;

public class TradeProcessorService implements TradeProcessor {
    private static TradeProcessorService instance;
    Logger logger = Logger.getLogger(TradeProcessorService.class.getName());
    private TransactionUtil transactionUtil;
    private TradePayloadRepository tradePayloadRepository;
    private LookupSecuritiesRepository lookupSecuritiesRepository;
    private JournalEntryRepository journalEntryRepository;
    private PositionsRepository positionsRepository;

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
            Optional<TradePayloadDTO> optionalTradePayload = tradePayloadRepository.readRawPayload(tradeId);
            optionalTradePayload.ifPresent(this::processTradePayload);
            transactionUtil.commitTransaction();
        } catch (HibernateException | OptimisticLockException | OptimisticLockingException e) {
            logger.warning("Hibernate/Optimistic Lock exception detected.");
            transactionUtil.rollbackTransaction();
            BeanFactory.getTradeProcessingRetryer().retryTradeProcessing(tradeId, queueName);
        }
    }

    void processTradePayload(TradePayloadDTO tradePayloadDTO) {
        String[] payloadArr = tradePayloadDTO.getPayload().split(",");
        String cusip = payloadArr[3];
        boolean validSecurity = lookupSecuritiesRepository.lookupSecurities(cusip);
        tradePayloadRepository.updateTradePayloadLookupStatus(validSecurity, tradePayloadDTO.getId());
        if (validSecurity) {
            JournalEntryDTO journalEntry = executeJournalEntryTransaction(payloadArr, tradePayloadDTO.getId());
            executePositionTransaction(journalEntry);
        }
    }

    @Override
    public JournalEntryDTO executeJournalEntryTransaction(String[] payloadArr, Long tradeId) {
        JournalEntryDTO journalEntry = JournalEntryDTO.builder()
                .tradeId(payloadArr[0])
                .accountNumber(payloadArr[2])
                .securityCusip(payloadArr[3])
                .direction(payloadArr[4])
                .quantity(Integer.parseInt(payloadArr[5]))
                .transactionTimestamp(payloadArr[1])
                .build();

        Optional<Long> optionalJournalEntryId = journalEntryRepository.saveJournalEntry(journalEntry);
        optionalJournalEntryId
                .ifPresent(journalEntry::setId);
        tradePayloadRepository.updateTradePayloadPostedStatus(tradeId);
        return journalEntry;
    }

    @Override
    public void executePositionTransaction(JournalEntryDTO journalEntry) {
        PositionDTO positionDTO = new PositionDTO();
        positionDTO.setAccountNumber(journalEntry.getAccountNumber());
        positionDTO.setSecurityCusip(journalEntry.getSecurityCusip());
        positionDTO.setHolding(
                (long) (journalEntry.getDirection().equals(Direction.SELL.toString()) ? -journalEntry.getQuantity()
                        : journalEntry.getQuantity()));
        positionsRepository.upsertPosition(positionDTO);
        journalEntryRepository.updateJournalEntryStatus(journalEntry.getId());
    }
}
