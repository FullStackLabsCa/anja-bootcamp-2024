package io.reactivestax.service;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import io.reactivestax.utility.hibernate.HibernateConnectionUtil;
import io.reactivestax.entity.TradePayload;
import io.reactivestax.enums.ValidityStatusEnum;
import io.reactivestax.utility.rabbitmq.QueueUtil;
import io.reactivestax.repository.hibernate.HibernateTradePayloadRepositoryRepository;
import io.reactivestax.utility.ApplicationPropertiesUtils;
import org.hibernate.HibernateException;
import org.hibernate.Session;

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
        ConnectionFactory connectionFactory =
                QueueUtil.getInstance(applicationPropertiesUtils).getQueueConnectionFactory();
        try (Session session = HibernateConnectionUtil.getSessionFactory().openSession();
             BufferedReader reader = new BufferedReader(new FileReader(filePath));
             Connection connection = connectionFactory.newConnection();
             Channel channel = connection.createChannel()) {
            channel.exchangeDeclare(applicationPropertiesUtils.getQueueExchangeName(),
                    applicationPropertiesUtils.getQueueExchangeType());
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
                HibernateTradePayloadRepositoryRepository hibernateTradePayloadRepository = new HibernateTradePayloadRepositoryRepository();
                hibernateTradePayloadRepository.insertTradeRawPayload(tradePayload, session);
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
        } catch (TimeoutException e) {
            logger.warning("RabbitMQ timeout exception detected.");
        } catch (IOException e) {
            logger.warning("File not found.");
        } catch (HibernateException e) {
            logger.warning("Hibernate exception detected.");
        }
    }
}
