package io.reactivestax.service;

import io.reactivestax.database.DBUtils;
import io.reactivestax.model.RawPayload;
import io.reactivestax.repository.TradePayloadRepository;
import io.reactivestax.utility.ApplicationPropertiesUtils;

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
    int count = 0;

    public ChunkProcessor() {
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
        Connection connection = DBUtils.getInstance().getConnection();
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            RawPayload rawPayload = new RawPayload();
            String payload;
            while ((payload = reader.readLine()) != null) {
                rawPayload.setPayload(payload);
                String[] transaction = payload.split(",");
                rawPayload.setTradeId(transaction[0]);
                rawPayload.setValidityStatus("valid");
                if (transaction.length != 7) {
                    rawPayload.setValidityStatus("invalid");
                }
                TradePayloadRepository tradePayloadRepository = new TradePayloadRepository();
                // inserts to raw_payloads table
                tradePayloadRepository.insertTradeRawPayload(rawPayload, connection);
                // inserts to concurrent hash map and get the queue number
                if (rawPayload.getValidityStatus().equals("valid")) {
                    int queueNumber = QueueDistributor.figureOutTheNextQueue(ApplicationPropertiesUtils.getTradeDistributionCriteria().equals(
                            "accountNumber") ? transaction[2] : rawPayload.getTradeId());
                    // inserts to the queue number found in above step
                    QueueDistributor.giveToTradeQueue(rawPayload.getTradeId(), queueNumber);
                }
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } catch (IOException e) {
            logger.warning("File not found.");
        } catch (SQLException e) {
            connection.rollback();
            connection.setAutoCommit(true);
        } finally {
            connection.close();
        }
    }
}
