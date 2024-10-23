package io.reactivestax.producer.service;

import io.reactivestax.producer.repository.TradePayloadRepository;
import io.reactivestax.producer.type.entity.TradePayload;
import io.reactivestax.producer.type.enums.ValidityStatus;
import io.reactivestax.producer.util.ApplicationPropertiesUtils;
import io.reactivestax.producer.util.QueueProvider;
import io.reactivestax.producer.util.database.TransactionUtil;
import io.reactivestax.producer.util.factory.BeanFactory;
import io.reactivestax.producer.util.messaging.MessageSender;
import io.reactivestax.producer.util.messaging.QueueDistributor;
import org.hibernate.HibernateException;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.SQLException;
import java.util.logging.Logger;

public class ChunkFileProcessorService implements Runnable, ChunkProcessorService {
    Logger logger = Logger.getLogger(ChunkFileProcessorService.class.getName());

    @Override
    public void run() {
        try {
            while (true) {
                String filePath = QueueProvider.getChunkQueue().take();
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
        TransactionUtil transactionUtil = BeanFactory.getTransactionUtil();
        MessageSender messageSender = BeanFactory.getMessageSender();
        TradePayloadRepository tradePayloadRepository = BeanFactory.getTradePayloadRepository();

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String payload;
            while ((payload = reader.readLine()) != null) {
                String[] transaction = payload.split(",");
                TradePayload tradePayload = prepareTradePayload(payload, transaction);

                transactionUtil.startTransaction();
                tradePayloadRepository.insertTradeRawPayload(tradePayload);
                transactionUtil.commitTransaction();

                submitValidTradePayloadsToQueue(tradePayload, transaction, messageSender);
            }
        } catch (IOException | HibernateException e) {
            transactionUtil.rollbackTransaction();
            logger.warning("Exception detected in Chunk Processor.");
        }
    }

    private void submitValidTradePayloadsToQueue(TradePayload tradePayload, String[] transaction,
                                                 MessageSender messageSender) {
        if (tradePayload.getValidityStatus().equals(ValidityStatus.VALID)) {
            ApplicationPropertiesUtils applicationPropertiesUtils = ApplicationPropertiesUtils.getInstance();
            String queueName = applicationPropertiesUtils.getQueueExchangeName() + "_queue_"
                    + QueueDistributor.figureOutTheNextQueue(
                    applicationPropertiesUtils.getTradeDistributionCriteria().equals("accountNumber")
                            ? transaction[2]
                            : tradePayload.getTradeNumber(),
                    applicationPropertiesUtils.isTradeDistributionUseMap(),
                    applicationPropertiesUtils.getTradeDistributionAlgorithm(),
                    applicationPropertiesUtils.getTradeProcessorQueueCount());
            messageSender.sendMessage(queueName, tradePayload.getTradeNumber());
        }
    }

    private static TradePayload prepareTradePayload(String payload, String[] transaction) {
        TradePayload tradePayload = new TradePayload();
        tradePayload.setPayload(payload);
        tradePayload.setTradeNumber(transaction[0]);
        tradePayload.setValidityStatus(ValidityStatus.VALID);
        if (transaction.length != 7) {
            tradePayload.setValidityStatus(ValidityStatus.INVALID);
        }
        return tradePayload;
    }
}
