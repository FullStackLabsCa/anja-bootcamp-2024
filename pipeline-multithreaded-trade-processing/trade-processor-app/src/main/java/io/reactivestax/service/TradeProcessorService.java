package io.reactivestax.service;

import io.reactivestax.utility.ApplicationPropertiesUtils;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TradeProcessorService implements Submittable<TradeProcessor> {

    ExecutorService tradeProcessorExecutorService = Executors.newFixedThreadPool(ApplicationPropertiesUtils.getTradeProcessorThreadCount());

    public void submitTrade() {
        for (int i = 0; i < ApplicationPropertiesUtils.getTradeProcessorQueueCount(); i++) {
            submitTask(new TradeProcessor(QueueDistributor.getTransactionDeque(i)));
        }
        tradeProcessorExecutorService.shutdown();
    }

    @Override
    public void submitTask(TradeProcessor tradeProcessor) {
        tradeProcessorExecutorService.submit(tradeProcessor);
    }
}
