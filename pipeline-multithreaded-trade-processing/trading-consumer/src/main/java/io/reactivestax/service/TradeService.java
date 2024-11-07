package io.reactivestax.service;

import io.reactivestax.task.FileTradeProcessor;
import io.reactivestax.util.ApplicationPropertiesUtils;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Logger;
import java.util.stream.IntStream;

public class TradeService {
    private static TradeService instance;
    private final ExecutorService tradeProcessorExecutorService = Executors.newFixedThreadPool(ApplicationPropertiesUtils.getInstance().getTradeProcessorThreadCount());
    ApplicationPropertiesUtils applicationPropertiesUtils = ApplicationPropertiesUtils.getInstance();
    Logger logger = Logger.getLogger(TradeService.class.getName());

    private TradeService() {
    }

    public static synchronized TradeService getInstance() {
        if (instance == null) {
            instance = new TradeService();
        }

        return instance;
    }

    public void startTradeConsumer() {
        IntStream.range(0, this.applicationPropertiesUtils.getTradeProcessorQueueCount())
                .forEach(i -> new FileTradeProcessor(applicationPropertiesUtils.getQueueExchangeName() +
                        "_queue_" + i));
        logger.info("Started trade processor.");
        addShutdownHook();
    }

    private void addShutdownHook() {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            logger.info("Shutdown signal received. Stopping consumer...");
            tradeProcessorExecutorService.shutdownNow();
            logger.info("Consumer stopped.");
        }));
    }
}
