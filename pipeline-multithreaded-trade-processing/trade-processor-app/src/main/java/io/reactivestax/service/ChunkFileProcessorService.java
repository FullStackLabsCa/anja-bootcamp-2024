package io.reactivestax.service;

import io.reactivestax.factory.BeanFactory;
import io.reactivestax.repository.TradePayloadRepository;
import io.reactivestax.utility.database.TransactionUtil;
import io.reactivestax.entity.TradePayload;
import io.reactivestax.enums.ValidityStatusEnum;
import io.reactivestax.utility.messaging.QueueMessageSender;
import io.reactivestax.utility.ApplicationPropertiesUtils;
import org.hibernate.HibernateException;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.SQLException;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeoutException;
import java.util.logging.Logger;

public class ChunkFileProcessorService implements Runnable, ChunkProcessorService {
    Logger logger = Logger.getLogger(ChunkFileProcessorService.class.getName());
    LinkedBlockingQueue<String> chunkQueue = QueueDistributor.chunkQueue;

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
        TransactionUtil transactionUtil = BeanFactory.getTransactionUtil();
        QueueMessageSender queueMessageSender = BeanFactory.getQueueMessageSender();
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String payload;
            while ((payload = reader.readLine()) != null) {
                String[] transaction = payload.split(",");
                TradePayload tradePayload = prepareTradePayload(payload, transaction);

                TradePayloadRepository tradePayloadRepository = BeanFactory.getTradePayloadRepository();
                transactionUtil.startTransaction();
                tradePayloadRepository.insertTradeRawPayload(tradePayload);
                transactionUtil.commitTransaction();

                submitValidTradePayloadsToQueue(tradePayload, transaction, queueMessageSender);
            }
        } catch (IOException | HibernateException | TimeoutException e) {
            transactionUtil.rollbackTransaction();
            logger.warning("Exception detected in Chunk Processor.");
            throw new RuntimeException(e);
        }catch (Exception e){
            e.printStackTrace();
        }
    }


    private void submitValidTradePayloadsToQueue(TradePayload tradePayload, String[] transaction, QueueMessageSender queueMessageSender) throws IOException, TimeoutException {
        if (tradePayload.getValidityStatus().equals(ValidityStatusEnum.VALID)) {
            ApplicationPropertiesUtils applicationPropertiesUtils = ApplicationPropertiesUtils.getInstance();
            String queueName = applicationPropertiesUtils.getQueueExchangeName() + "_queue_" + QueueDistributor.figureOutTheNextQueue(applicationPropertiesUtils.getTradeDistributionCriteria().equals("accountNumber") ? transaction[2] : tradePayload.getTradeNumber(), applicationPropertiesUtils.isTradeDistributionUseMap(), applicationPropertiesUtils.getTradeDistributionAlgorithm(), applicationPropertiesUtils.getTradeProcessorQueueCount());
            queueMessageSender.sendMessageToQueue(queueName, tradePayload.getTradeNumber());
        }
    }

    private static TradePayload prepareTradePayload(String payload, String[] transaction) {
        TradePayload tradePayload = new TradePayload();
        tradePayload.setPayload(payload);
        tradePayload.setTradeNumber(transaction[0]);
        tradePayload.setValidityStatus(ValidityStatusEnum.VALID);
        if (transaction.length != 7) {
            tradePayload.setValidityStatus(ValidityStatusEnum.INVALID);
        }
        return tradePayload;
    }
}
