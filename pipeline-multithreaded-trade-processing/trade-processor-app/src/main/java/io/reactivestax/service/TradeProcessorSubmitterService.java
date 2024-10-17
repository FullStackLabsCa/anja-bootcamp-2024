package io.reactivestax.service;

import io.reactivestax.utility.ApplicationPropertiesUtils;
import io.reactivestax.utility.messaging.Submittable;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Logger;

public class TradeProcessorSubmitterService implements Submittable<FileTradeProcessorService> {
    Logger logger = Logger.getLogger(TradeProcessorSubmitterService.class.getName());
    ExecutorService tradeProcessorExecutorService;
    ApplicationPropertiesUtils applicationPropertiesUtils;

    public TradeProcessorSubmitterService() {
        this.applicationPropertiesUtils = ApplicationPropertiesUtils.getInstance();
        tradeProcessorExecutorService = Executors.newFixedThreadPool(applicationPropertiesUtils.getTradeProcessorThreadCount());
    }

    public void submitTrade() {
        for (int i = 0; i < this.applicationPropertiesUtils.getTradeProcessorQueueCount(); i++) {
            submitTask(new FileTradeProcessorService(applicationPropertiesUtils.getQueueExchangeName() + "_queue_" + i));
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
