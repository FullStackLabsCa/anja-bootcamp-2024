package io.reactivestax.service;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import io.reactivestax.factory.BeanFactory;
import io.reactivestax.repository.TradePayloadRepository;
import io.reactivestax.utility.TransactionUtil;
import io.reactivestax.entity.TradePayload;
import io.reactivestax.enums.ValidityStatusEnum;
import io.reactivestax.utility.rabbitmq.QueueUtil;
import io.reactivestax.utility.ApplicationPropertiesUtils;
import jakarta.transaction.Transactional;
import org.hibernate.HibernateException;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeoutException;
import java.util.logging.Logger;

public class ChunkFileProcessorService implements Runnable, ChunkProcessorService {
    Logger logger = Logger.getLogger(ChunkFileProcessorService.class.getName());
    LinkedBlockingQueue<String> chunkQueue = QueueDistributor.chunkQueue;
    ApplicationPropertiesUtils applicationPropertiesUtils;

    public ChunkFileProcessorService(ApplicationPropertiesUtils applicationPropertiesUtils) {
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
        TransactionUtil transactionUtil = BeanFactory.getTransactionUtil();
        //
        ConnectionFactory connectionFactory =
                QueueUtil.getInstance(applicationPropertiesUtils).getQueueConnectionFactory();
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath));
             Connection connection = connectionFactory.newConnection();
             Channel channel = connection.createChannel()) {
                    channel.exchangeDeclare(applicationPropertiesUtils.getQueueExchangeName(),applicationPropertiesUtils.getQueueExchangeType());

                    String payload;
                    while ((payload = reader.readLine()) != null) {
                        String[] transaction = payload.split(",");
                        TradePayload tradePayload = prepareTradePayload(payload, transaction);

                        TradePayloadRepository tradePayloadRepository = BeanFactory.getTradePayloadRepository();

                        transactionUtil.startTransaction();
                        tradePayloadRepository.insertTradeRawPayload(tradePayload);
                        transactionUtil.commitTransaction();

                        submitValidTradePayloadsToQueue(tradePayload, transaction, channel);
                    }

        } catch (TimeoutException | IOException | HibernateException e) {
            transactionUtil.rollbackTransaction();
            logger.warning("Exception detected in Chunk Processor.");
            throw new RuntimeException(e);
        }
    }

    private void submitValidTradePayloadsToQueue(TradePayload tradePayload, String[] transaction, Channel channel) throws IOException {
        if (tradePayload.getValidityStatus().equals(ValidityStatusEnum.VALID)) {
            String routingKey = "trade_processor_queue" + QueueDistributor.figureOutTheNextQueue(
                    this.applicationPropertiesUtils.getTradeDistributionCriteria().equals("accountNumber") ? transaction[2] : tradePayload.getTradeNumber(),
                    this.applicationPropertiesUtils.isTradeDistributionUseMap(),
                    this.applicationPropertiesUtils.getTradeDistributionAlgorithm(),
                    this.applicationPropertiesUtils.getTradeProcessorQueueCount()
            );
            channel.basicPublish(applicationPropertiesUtils.getQueueExchangeName(), routingKey, null,
                    tradePayload.getTradeNumber().getBytes(StandardCharsets.UTF_8));
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
