package io.reactivestax.service;

import io.reactivestax.database.HibernateUtil;
import io.reactivestax.entity.TradePayload;
import io.reactivestax.enums.ValidityStatusEnum;
import io.reactivestax.repository.TradePayloadRepository;
import io.reactivestax.utility.ApplicationPropertiesUtils;
import org.hibernate.HibernateException;
import org.hibernate.Session;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.SQLException;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Logger;

public class ChunkProcessor implements Runnable, ProcessChunk {
    Logger logger = Logger.getLogger(ChunkProcessor.class.getName());
    LinkedBlockingQueue<String> chunkQueue = QueueDistributor.chunkQueue;
    ApplicationPropertiesUtils applicationPropertiesUtils;

    public ChunkProcessor(ApplicationPropertiesUtils applicationPropertiesUtils) {
        this.applicationPropertiesUtils = applicationPropertiesUtils;
    }

    @Override
    public void run() {
        try {
            while (true) {
                String filePath = this.chunkQueue.take();
                if (!filePath.isEmpty()) {
                    processChunk(filePath);
                    break;
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
        try (Session session = HibernateUtil.getSessionFactory().openSession(); BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String payload;
            while ((payload = reader.readLine()) != null) {
                TradePayload tradePayload = new TradePayload();
                tradePayload.setPayload(payload);
                String[] transaction = payload.split(",");
                tradePayload.setTradeNumber(transaction[0]);
                tradePayload.setValidityStatus(ValidityStatusEnum.VALID);
                if (transaction.length != 7) {
                    tradePayload.setValidityStatus(ValidityStatusEnum.INVALID);
                }
                TradePayloadRepository tradePayloadRepository = new TradePayloadRepository();
                tradePayloadRepository.insertTradeRawPayload(tradePayload, session);
                if (tradePayload.getValidityStatus().equals(ValidityStatusEnum.VALID)) {
                    int queueNumber = QueueDistributor.figureOutTheNextQueue(
                            this.applicationPropertiesUtils.getTradeDistributionCriteria().equals("accountNumber") ? transaction[2] : tradePayload.getTradeNumber(),
                            this.applicationPropertiesUtils.isTradeDistributionUseMap(),
                            this.applicationPropertiesUtils.getTradeDistributionAlgorithm(),
                            this.applicationPropertiesUtils.getTradeProcessorQueueCount()
                    );
                    QueueDistributor.giveToTradeQueue(tradePayload.getTradeNumber(), queueNumber);
                }
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } catch (IOException e) {
            logger.warning("File not found.");
        } catch (HibernateException e) {
            logger.warning("Hibernate exception detected.");
        }
    }
}
