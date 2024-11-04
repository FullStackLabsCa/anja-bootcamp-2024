package io.reactivestax.service;

import io.reactivestax.repository.TradePayloadRepository;
import io.reactivestax.task.ChunkProcessor;
import io.reactivestax.type.dto.TradePayload;
import io.reactivestax.type.enums.ValidityStatus;
import io.reactivestax.util.ApplicationPropertiesUtils;
import io.reactivestax.util.database.TransactionUtil;
import io.reactivestax.util.factory.BeanFactory;
import io.reactivestax.util.messaging.MessageSender;
import io.reactivestax.util.messaging.QueueDistributor;
import org.hibernate.HibernateException;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.SQLException;
import java.util.logging.Logger;
import java.util.stream.Stream;

public class ChunkProcessorService implements ChunkProcessor {
    private static ChunkProcessorService instance;
    Logger logger = Logger.getLogger(ChunkProcessorService.class.getName());

    private ChunkProcessorService() {
    }

    public static synchronized ChunkProcessorService getInstance() {
        if (instance == null) {
            instance = new ChunkProcessorService();
        }

        return instance;
    }

    @Override
    public void processChunk(String filePath) throws SQLException {
        TransactionUtil transactionUtil = BeanFactory.getTransactionUtil();
        MessageSender messageSender = BeanFactory.getMessageSender();
        TradePayloadRepository tradePayloadRepository = BeanFactory.getTradePayloadRepository();

        try (Stream<String> lines = Files.lines(Path.of(filePath), StandardCharsets.UTF_8)) {

            lines.filter(line -> !line.trim().isEmpty()).forEach(payload -> {
                String[] transaction = payload.split(",");
                TradePayload tradePayload = prepareTradePayload(payload, transaction);
                transactionUtil.startTransaction();
                tradePayloadRepository.insertTradeRawPayload(tradePayload);
                transactionUtil.commitTransaction();
                submitValidTradePayloadsToQueue(tradePayload, transaction, messageSender);
            });

        } catch (IOException | HibernateException e) {
            transactionUtil.rollbackTransaction();
            logger.warning("Exception detected in Chunk Processor.");
        }
    }

    private void submitValidTradePayloadsToQueue(TradePayload tradePayload, String[] transaction,
                                                 MessageSender messageSender) {
        if (tradePayload.getValidityStatus().equals(ValidityStatus.VALID.toString())) {
            ApplicationPropertiesUtils applicationPropertiesUtils = ApplicationPropertiesUtils.getInstance();
            String queueName = applicationPropertiesUtils.getQueueExchangeName() + "_queue_"
                    + QueueDistributor.getInstance().figureOutTheNextQueue(
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
        tradePayload.setValidityStatus(String.valueOf(ValidityStatus.VALID));
        if (transaction.length != 7) {
            tradePayload.setValidityStatus(String.valueOf(ValidityStatus.INVALID));
        }
        return tradePayload;
    }
}
