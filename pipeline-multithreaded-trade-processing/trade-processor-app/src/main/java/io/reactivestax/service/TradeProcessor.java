package io.reactivestax.service;

import io.reactivestax.database.DBUtils;
import io.reactivestax.model.JournalEntry;
import io.reactivestax.model.Position;
import io.reactivestax.repository.PositionsRepository;
import io.reactivestax.repository.SecuritiesReferenceRepository;
import io.reactivestax.repository.TradePayloadRepository;
import io.reactivestax.repository.JournalEntryRepository;
import io.reactivestax.utility.ApplicationPropertiesUtils;

import java.sql.Connection;
import java.sql.SQLException;
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
    int count = 0;
    private final Map<String, Integer> retryCountMap;
    private Connection connection;
    ApplicationPropertiesUtils applicationPropertiesUtils;

    public TradeProcessor(LinkedBlockingDeque<String> tradeDeque, ApplicationPropertiesUtils applicationPropertiesUtils) {
        this.tradeDeque = tradeDeque;
        this.retryCountMap = new HashMap<>();
        this.applicationPropertiesUtils = applicationPropertiesUtils;
    }

    public void setConnection(Connection connection){
        this.connection = connection;
    }

    public LinkedBlockingDeque<String> getTradeDeque() {
        return this.tradeDeque;
    }

    public void setRetryCountMap(String key, Integer value) {
        this.retryCountMap.put(key, value);
    }

    public Map<String, Integer> getRetryCountMap() {
        return this.retryCountMap;
    }

    @Override
    public void run() {
        count++;
        try {
            this.connection = DBUtils.getInstance(this.applicationPropertiesUtils).getConnection();
            while (true) {
                String tradeId = this.tradeDeque.poll(500, TimeUnit.MILLISECONDS);
                if (tradeId == null) break;
                else processTrade(tradeId);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            logger.warning("Thread was interrupted.");
        } catch (SQLException e) {
            logger.warning("Exception in database query.");
        }finally {
            try {
                this.connection.close();
            } catch (SQLException e) {
                logger.warning("Exception in closing the connection with DB connection pool");
            }
        }
    }

    @Override
    public void processTrade(String tradeId) throws SQLException, InterruptedException {
        try {
            TradePayloadRepository tradePayloadRepository = new TradePayloadRepository();
            String payload = tradePayloadRepository.readRawPayload(tradeId, this.connection);
            String[] payloadArr = payload.split(",");
            String cusip = payloadArr[3];
            SecuritiesReferenceRepository securitiesReferenceRepository = new SecuritiesReferenceRepository();
            boolean validSecurity = securitiesReferenceRepository.lookupSecurities(cusip, this.connection);
            tradePayloadRepository.updateTradePayloadLookupStatus(validSecurity, tradeId, this.connection);
            if (validSecurity) {
                JournalEntry journalEntry = journalEntryTransaction(payloadArr, cusip);
                positionTransaction(journalEntry);
            } else QueueDistributor.deadLetterTransactionDeque.put(tradeId);
        } catch (SQLException e) {
            logger.info("Exception in SQL.");
            this.connection.rollback();
            retryTransaction(tradeId);
        } finally {
            this.connection.setAutoCommit(true);
        }
    }

    @Override
    public JournalEntry journalEntryTransaction(String[] payloadArr, String cusip) throws SQLException {
        JournalEntry journalEntry = new JournalEntry(
                payloadArr[0],
                payloadArr[2],
                cusip,
                payloadArr[4],
                Integer.parseInt(payloadArr[5]),
                "not_posted",
                LocalDateTime.parse(payloadArr[1], DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
        );
        JournalEntryRepository journalEntryRepository = new JournalEntryRepository();
        journalEntryRepository.insertIntoJournalEntry(journalEntry, this.connection);
        TradePayloadRepository tradePayloadRepository = new TradePayloadRepository();
        tradePayloadRepository.updateTradePayloadPostedStatus("posted", journalEntry.tradeId(), this.connection);
        return journalEntry;
    }

    @Override
    public void positionTransaction(JournalEntry journalEntry) throws SQLException {
        Position position = new Position(journalEntry.accountNumber(), journalEntry.securityCusip(),
                journalEntry.quantity(), 0);
        PositionsRepository positionsRepository = new PositionsRepository();
        int[] positionsAndVersion = positionsRepository.lookupPositions(position, this.connection);
        position.setVersion(positionsAndVersion[1]);
        if (journalEntry.direction().equalsIgnoreCase("BUY")) {
            position.setPositions(positionsAndVersion[0] + journalEntry.quantity());
        } else position.setPositions(positionsAndVersion[0] - journalEntry.quantity());
        if (position.getVersion() == 0) {
            positionsRepository.insertIntoPositions(position, this.connection);
        } else positionsRepository.updatePositions(position, this.connection);
        JournalEntryRepository journalEntryRepository = new JournalEntryRepository();
        journalEntryRepository.updateJournalEntryStatus(journalEntry.tradeId(), this.connection);
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
