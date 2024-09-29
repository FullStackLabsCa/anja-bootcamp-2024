package io.reactivestax.service;


import com.zaxxer.hikari.HikariDataSource;
import io.reactivestax.model.JournalEntry;
import io.reactivestax.model.Position;
import io.reactivestax.repository.TradeRepository;
import io.reactivestax.utility.MaintainStaticCounts;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.LinkedBlockingDeque;

public class TradeProcessor implements Runnable {

    LinkedBlockingDeque<String> tradeDeque;
    int count = 0;
    HikariDataSource hikariDataSource;
    private final Map<String, Integer> retryCountMap;

    public TradeProcessor(LinkedBlockingDeque<String> tradeDeque, HikariDataSource hikariDataSource) {
        this.tradeDeque = tradeDeque;
        this.hikariDataSource = hikariDataSource;
        this.retryCountMap = new HashMap<>();
    }

    @Override
    public void run() {
        count++;
        while (count < MaintainStaticCounts.getRowsPerFile() + MaintainStaticCounts.getNumberOfChunks()) {
            try {
                processTrade(this.tradeDeque.take());
            } catch (InterruptedException | SQLException e) {
                Thread.currentThread().interrupt();
                System.out.println("Thread was interrupted");
            }
        }
    }

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
            connection.rollback();
            retryTransaction(tradeId);
        } finally {
            connection.setAutoCommit(true);
            connection.close();
        }
    }

    public JournalEntry journalEntryTransaction(String[] payloadArr, String cusip, TradeRepository tradeRepository,
                                        Connection connection) throws SQLException{
        JournalEntry journalEntry = new JournalEntry(
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

    public void positionTransaction(JournalEntry journalEntry, TradeRepository tradeRepository,
                                    Connection connection) throws SQLException{
        Position position = new Position(journalEntry.getAccountNumber(), journalEntry.getSecurityCusip(),
                journalEntry.getQuantity());
        int quantity = tradeRepository.lookupPositions(position, connection);
        if (journalEntry.getDirection().equalsIgnoreCase("BUY")) {
            position.setQuantity(quantity + journalEntry.getQuantity());
        } else position.setQuantity(quantity - journalEntry.getQuantity());
        tradeRepository.upsertIntoPositions(position, connection);
    }

    public void retryTransaction(String tradeId) throws InterruptedException{
        int maxRetries = 3;
        int retryCount = this.retryCountMap.getOrDefault(tradeId, 0) + 1;
        if (retryCount >= maxRetries) {
            QueueDistributor.deadLetterTransactionDeque.putLast(tradeId);
            this.retryCountMap.remove(tradeId);
        } else {
            this.tradeDeque.putFirst(tradeId);
            this.retryCountMap.put(tradeId, retryCount);
        }
    }
}
