package io.reactivestax.service;

import com.zaxxer.hikari.HikariDataSource;
import io.reactivestax.model.JournalEntry;
import io.reactivestax.model.Position;
import io.reactivestax.repository.TradeRepository;
import io.reactivestax.utility.MaintainStaticValues;

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
    HikariDataSource hikariDataSource;
    private final Map<String, Integer> retryCountMap;

    public TradeProcessor(LinkedBlockingDeque<String> tradeDeque, HikariDataSource hikariDataSource) {
        this.tradeDeque = tradeDeque;
        this.hikariDataSource = hikariDataSource;
        this.retryCountMap = new HashMap<>();
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
        while (count < MaintainStaticValues.getTotalNoOfLines() + MaintainStaticValues.getNumberOfChunks()) {
            try {
                String tradeId = this.tradeDeque.poll(500, TimeUnit.MILLISECONDS);
                if(tradeId==null) break;
                 else processTrade(tradeId);
            } catch (InterruptedException | SQLException e) {
                Thread.currentThread().interrupt();
                logger.warning("Thread was interrupted");
            }
        }
    }

    @Override
    public void processTrade(String tradeId) throws SQLException, InterruptedException {
        TradeRepository tradeRepository = new TradeRepository();
        Connection connection = hikariDataSource.getConnection();
        try {
            String payload = tradeRepository.readRawPayload(tradeId, connection);
            String[] payloadArr = payload.split(",");
            String cusip = payloadArr[3];
            boolean validSecurity = tradeRepository.lookupSecurities(cusip, connection);
            if (validSecurity) {
                JournalEntry journalEntry = journalEntryTransaction(payloadArr, cusip, tradeRepository, connection);
                positionTransaction(journalEntry, tradeRepository, connection);
            }
        } catch (SQLException e) {
            logger.info("Exception in SQL.");
            connection.rollback();
            retryTransaction(tradeId);
        } finally {
            connection.setAutoCommit(true);
            connection.close();
        }
    }

    @Override
    public JournalEntry journalEntryTransaction(String[] payloadArr, String cusip, TradeRepository tradeRepository,
                                                Connection connection) throws SQLException {
        JournalEntry journalEntry = new JournalEntry(
                payloadArr[0],
                payloadArr[2],
                cusip,
                payloadArr[4],
                Integer.parseInt(payloadArr[5]),
                "NOT_POSTED",
                LocalDateTime.parse(payloadArr[1], DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
        );
        tradeRepository.insertIntoJournalEntry(journalEntry, connection);
        return journalEntry;
    }

    @Override
    public void positionTransaction(JournalEntry journalEntry, TradeRepository tradeRepository,
                                    Connection connection) throws SQLException {
        Position position = new Position(journalEntry.accountNumber(), journalEntry.securityCusip(),
                journalEntry.quantity(), 0);
        int[] positionsAndVersion = tradeRepository.lookupPositions(position, connection);
        position.setVersion(positionsAndVersion[1]);
        if (journalEntry.direction().equalsIgnoreCase("BUY")) {
            position.setPositions(positionsAndVersion[0] + journalEntry.quantity());
        } else position.setPositions(positionsAndVersion[0] - journalEntry.quantity());
        if (position.getVersion() == 0) {
            tradeRepository.insertIntoPositions(position, connection);
        } else tradeRepository.updatePositions(position, connection);
        tradeRepository.updateJournalEntryStatus(journalEntry.tradeId(), connection);
    }

    @Override
    public void retryTransaction(String tradeId) throws InterruptedException {
        int retryCount = this.retryCountMap.getOrDefault(tradeId, 0) + 1;
        if (retryCount >= MaintainStaticValues.getMaxRetryCount()) {
            QueueDistributor.deadLetterTransactionDeque.putLast(tradeId);
            this.retryCountMap.remove(tradeId);
        } else {
            this.tradeDeque.putFirst(tradeId);
           setRetryCountMap(tradeId, retryCount);
        }
    }
}
