package io.reactivestax.consumer.service;

import io.reactivestax.consumer.util.ApplicationPropertiesUtils;
import io.reactivestax.consumer.util.messaging.Submittable;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Logger;

public class TradeService implements Submittable<FileTradeProcessor> {
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
        for (int i = 0; i < this.applicationPropertiesUtils.getTradeProcessorQueueCount(); i++) {
            submitTask(new FileTradeProcessor(applicationPropertiesUtils.getQueueExchangeName() + "_queue_" + i));
        }
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

    @Override
    public void submitTask(FileTradeProcessor chunkProcessor) {
        tradeProcessorExecutorService.submit(chunkProcessor);
    }
}
