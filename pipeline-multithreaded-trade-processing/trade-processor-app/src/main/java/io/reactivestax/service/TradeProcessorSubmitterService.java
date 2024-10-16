package io.reactivestax.service;

import io.reactivestax.utility.ApplicationPropertiesUtils;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Logger;

public class TradeProcessorSubmitterService implements Submittable<FileTradeProcessorService> {
    Logger logger = Logger.getLogger(TradeProcessorSubmitterService.class.getName());
    ExecutorService tradeProcessorExecutorService;
    ApplicationPropertiesUtils applicationPropertiesUtils;

    public TradeProcessorSubmitterService(ApplicationPropertiesUtils applicationProperties) {
        this.applicationPropertiesUtils = applicationProperties;
        tradeProcessorExecutorService = Executors.newFixedThreadPool(applicationProperties.getTradeProcessorThreadCount());
    }

    public void submitTrade() {
        for (int i = 0; i < this.applicationPropertiesUtils.getTradeProcessorQueueCount(); i++) {
            submitTask(new FileTradeProcessorService(applicationPropertiesUtils.getQueueExchangeName() + "_queue_" + i, this.applicationPropertiesUtils));
        }
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            logger.info("Shutdown signal received. Stopping consumer...");
            tradeProcessorExecutorService.shutdownNow();
            logger.info("Consumer stopped.");
        }));
    }

    @Override
    public void submitTask(FileTradeProcessorService fileTradeProcessorService) {
        tradeProcessorExecutorService.submit(fileTradeProcessorService);
    }
}
