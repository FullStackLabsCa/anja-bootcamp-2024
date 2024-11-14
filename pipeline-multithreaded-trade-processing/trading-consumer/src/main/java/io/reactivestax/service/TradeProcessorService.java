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
import lombok.Setter;
import lombok.extern.log4j.Log4j2;

@Log4j2
@Setter
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
        logger.info(() -> String.format("Processing trade  -->: %s", tradeId));
        try {
            transactionUtil.startTransaction();
            logger.info(() -> "stage 1");
            Optional<TradePayloadDTO> optionalTradePayload = tradePayloadRepository.readRawPayload(tradeId);
            logger.info(() -> "stage 2");
            optionalTradePayload.ifPresent(this::processTradePayload);
            logger.info(() -> "stage 3");
            transactionUtil.commitTransaction();
            logger.info(() -> "stage 4");
        } catch (HibernateException | OptimisticLockException | OptimisticLockingException e) {
            logger.warning("Hibernate/Optimistic Lock exception detected.");
            transactionUtil.rollbackTransaction();
            BeanFactory.getTradeProcessingRetryer().retryTradeProcessing(tradeId, queueName);
        }
    }

    void processTradePayload(TradePayloadDTO tradePayloadDTO) {
        String[] payloadArr = tradePayloadDTO.getPayload().split(",");
        String cusip = payloadArr[3];
        logger.info(() -> "Processing trade payload for CUSIP: " + cusip);
        boolean validSecurity = lookupSecuritiesRepository.lookupSecurities(cusip);
        logger.info(() -> String.format("Security lookup for CUSIP: %s is %s", cusip, validSecurity));
        tradePayloadRepository.updateTradePayloadLookupStatus(validSecurity, tradePayloadDTO.getId());
        if (validSecurity) {
            logger.info(() -> "validSecurity = true ");
            JournalEntryDTO journalEntry = executeJournalEntryTransaction(payloadArr, tradePayloadDTO.getId());
            executePositionTransaction(journalEntry);
        } else {
            logger.info(() -> "validSecurity = false ");
        }
    }

    @Override
    public JournalEntryDTO executeJournalEntryTransaction(String[] payloadArr, Long tradeId) {
        logger.info(() -> "Executing Journal Entry Transaction");
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
                .ifPresentOrElse(journalEntryId -> {
                    logger.info(() -> "Journal Entry saved with ID: " + journalEntryId);
                    journalEntry.setId(journalEntryId);
                }, () -> logger.warning("Journal Entry not saved"));
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
