package io.reactivestax.service;

import io.reactivestax.utility.ApplicationPropertiesUtils;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Logger;

public class TradeProcessorService implements Submittable<TradeProcessor> {
    Logger logger = Logger.getLogger(TradeProcessorService.class.getName());
    ExecutorService tradeProcessorExecutorService;
    ApplicationPropertiesUtils applicationPropertiesUtils;

    public TradeProcessorService(ApplicationPropertiesUtils applicationProperties) {
        this.applicationPropertiesUtils = applicationProperties;
        tradeProcessorExecutorService = Executors.newFixedThreadPool(applicationProperties.getTradeProcessorThreadCount());
    }

    public void submitTrade() {
        for (int i = 0; i < this.applicationPropertiesUtils.getTradeProcessorQueueCount(); i++) {
            submitTask(new TradeProcessor("trade_processor_queue" + i, this.applicationPropertiesUtils));
        }
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            logger.info("Shutdown signal received. Stopping consumer...");
            tradeProcessorExecutorService.shutdownNow();
            logger.info("Consumer stopped.");
        }));
    }

    @Override
    public void submitTask(TradeProcessor tradeProcessor) {
        tradeProcessorExecutorService.submit(tradeProcessor);
    }
}
