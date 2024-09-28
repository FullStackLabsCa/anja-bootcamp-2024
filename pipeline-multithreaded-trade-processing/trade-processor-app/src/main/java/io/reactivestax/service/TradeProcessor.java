package io.reactivestax.service;


import com.zaxxer.hikari.HikariDataSource;
import io.reactivestax.repository.TradeRepository;
import io.reactivestax.utility.MaintainStaticCounts;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.concurrent.LinkedBlockingQueue;

public class TradeProcessor implements Runnable {

    LinkedBlockingQueue<String> tradeQueue;
    int count = 0;
    HikariDataSource hikariDataSource;

    public TradeProcessor(LinkedBlockingQueue<String> tradeQueue, HikariDataSource hikariDataSource) {
        this.tradeQueue = tradeQueue;
        this.hikariDataSource = hikariDataSource;
    }

    @Override
    public void run() {
        count++;
        while (count < MaintainStaticCounts.getRowsPerFile() + MaintainStaticCounts.getNumberOfChunks()) {
            try {
                processTrade(this.tradeQueue.take());
            } catch (InterruptedException | SQLException e) {
                Thread.currentThread().interrupt();
                System.out.println("Thread was interrupted");
            }
        }
    }

    public void processTrade(String tradeId) throws SQLException {
        TradeRepository tradeRepository = new TradeRepository();
        Connection connection = hikariDataSource.getConnection();
        try {
            String payload = tradeRepository.readRawPayload(tradeId, connection);
            String[] payloadArr = payload.split(",");
            System.out.println(payloadArr);
        } catch (SQLException e) {
            e.printStackTrace();
            connection.rollback();
        } finally {
            connection.setAutoCommit(true);
        }
    }
}
