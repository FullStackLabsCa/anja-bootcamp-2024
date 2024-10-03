package io.reactivestax.service;

import com.sun.tools.javac.Main;
import com.zaxxer.hikari.HikariDataSource;
import io.reactivestax.utility.MaintainStaticValues;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TradeProcessorService implements Submittable<TradeProcessor> {

    ExecutorService tradeProcessorExecutorService = Executors.newFixedThreadPool(MaintainStaticValues.getTradeProcessorThreadCount());
    HikariDataSource hikariDataSource;

    public void submitTrade(HikariDataSource hikariDataSource) {
        this.hikariDataSource = hikariDataSource;
        for (int i = 0; i < MaintainStaticValues.getTradeProcessorQueueCount(); i++) {
            submitTask(new TradeProcessor(QueueDistributor.getTransactionDeque(i), hikariDataSource));
        }
        tradeProcessorExecutorService.shutdown();
    }

    @Override
    public void submitTask(TradeProcessor tradeProcessor) {
        tradeProcessorExecutorService.submit(tradeProcessor);
    }
}
