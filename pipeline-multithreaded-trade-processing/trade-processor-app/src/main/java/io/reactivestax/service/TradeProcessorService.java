package io.reactivestax.service;

import com.zaxxer.hikari.HikariDataSource;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TradeProcessorService implements Submittable<TradeProcessor> {

    ExecutorService tradeProcessorExecutorService = Executors.newFixedThreadPool(3);
    HikariDataSource hikariDataSource;

    public void submitTrade(HikariDataSource hikariDataSource) {
        this.hikariDataSource = hikariDataSource;
        submitTask(new TradeProcessor(QueueDistributor.transactionDequeOne, hikariDataSource));
        submitTask(new TradeProcessor(QueueDistributor.transactionDequeTwo, hikariDataSource));
        submitTask(new TradeProcessor(QueueDistributor.transactionDequeThree, hikariDataSource));
    }

    @Override
    public void submitTask(TradeProcessor tradeProcessor) {
        tradeProcessorExecutorService.submit(tradeProcessor);
    }

}
