package io.reactivestax.service;

import com.zaxxer.hikari.HikariDataSource;
import io.reactivestax.model.RawPayload;
import io.reactivestax.repository.TradeRepository;
import io.reactivestax.utility.MaintainStaticValues;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Logger;

public class ChunkProcessor implements Runnable, ProcessChunk {
    Logger logger = Logger.getLogger(ChunkProcessor.class.getName());
    LinkedBlockingQueue<String> chunkQueue = QueueDistributor.chunkQueue;
    HikariDataSource hikariDataSource;
    int count = 0;

    public ChunkProcessor(HikariDataSource hikariDataSource) {
        this.hikariDataSource = hikariDataSource;
        this.count = 0;
    }

    @Override
    public void run() {
        try {
            while (this.count == 0) {
                String filePath = this.chunkQueue.take();
                if (!filePath.isEmpty()) {
                    processChunk(filePath);
                    count++;
                }
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } catch (SQLException e) {
            logger.warning("Something went wrong while establishing database connection.");
        }
    }

    @Override
    public void processChunk(String filePath) throws SQLException {
        Connection connection = hikariDataSource.getConnection();
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            RawPayload rawPayload = new RawPayload();
            String payload;
            while ((payload = reader.readLine()) != null) {
                rawPayload.setPayload(payload);
                String[] transaction = payload.split(",");
                rawPayload.setTradeId(transaction[0]);
                rawPayload.setStatus("valid");
                if (transaction.length != 7) {
                    rawPayload.setStatus("invalid");
                    rawPayload.setStatusReason("missing column(s)");
                }
                TradeRepository tradeRepository = new TradeRepository();
                // inserts to raw_payloads table
                tradeRepository.insertTradeRawPayload(rawPayload, connection);
                // inserts to concurrent hash map and get the queue number
                if (rawPayload.getStatus().equals("valid")) {
                    int queueNumber = QueueDistributor.figureOutTheNextQueue(MaintainStaticValues.getTradeDistributionCriteria().equals("accountNumber") ? transaction[2] : rawPayload.getTradeId());
                    // inserts to the queue number found in above step
                    QueueDistributor.giveToTradeQueue(rawPayload.getTradeId(), queueNumber);
                }
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } catch (IOException e) {
            logger.warning("File not found.");
        } catch (SQLException e) {
            System.out.println(e);
            connection.rollback();
            connection.setAutoCommit(true);
        } finally {
            connection.close();
        }
    }
}
