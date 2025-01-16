package io.reactivestax.tradingproducer.service;

import io.reactivestax.tradingproducer.task.ChunkProcessor;
import io.reactivestax.tradingproducer.type.entity.TradePayload;
import io.reactivestax.tradingproducer.type.enums.ValidityStatus;
import io.reactivestax.tradingproducer.util.ApplicationPropertiesUtils;
import io.reactivestax.tradingproducer.util.QueueProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.logging.Logger;
import java.util.stream.Stream;

@Service
public class ChunkProcessorService implements ChunkProcessor {

    private final ApplicationPropertiesUtils applicationPropertiesUtils;
    private final QueueProvider queueProvider;
    Logger logger = Logger.getLogger(ChunkProcessorService.class.getName());

    @Autowired
    public ChunkProcessorService(ApplicationPropertiesUtils applicationPropertiesUtils, QueueProvider queueProvider) {
        this.applicationPropertiesUtils = applicationPropertiesUtils;
        this.queueProvider = queueProvider;
    }

    @Override
    public void processChunk(String filePath) {

        try (Stream<String> lines = Files.lines(Path.of(filePath), StandardCharsets.UTF_8)) {

            lines.filter(line -> !line.trim().isEmpty()).forEach(payload -> {
                String[] transaction = payload.split(",");
                TradePayload tradePayload = prepareTradePayload(payload, transaction);
                System.out.println(tradePayload);
//                submitValidTradePayloadsToQueue(tradePayload, transaction);
            });
        } catch (IOException e) {
            logger.warning("Exception detected in Chunk Processor.");
        }
    }

    private void submitValidTradePayloadsToQueue(TradePayload tradePayload, String[] transaction) {
        if (tradePayload.getValidityStatus().equals(ValidityStatus.VALID.toString())) {
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
