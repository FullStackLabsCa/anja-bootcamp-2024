package io.reactivestax.service;

import com.zaxxer.hikari.HikariDataSource;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TradeProcessorService implements SubmitTaskInterface<TradeProcessor> {

    ExecutorService tradeProcessorExecutorService = Executors.newFixedThreadPool(3);
    HikariDataSource hikariDataSource;

    public void submitTrade(HikariDataSource hikariDataSource) {
        this.hikariDataSource = hikariDataSource;
        submitTask(new TradeProcessor(QueueDistributor.transactionQueueOne, hikariDataSource));
        submitTask(new TradeProcessor(QueueDistributor.transactionQueueTwo, hikariDataSource));
        submitTask(new TradeProcessor(QueueDistributor.transactionQueueThree, hikariDataSource));
    }

    @Override
    public void submitTask(TradeProcessor tradeProcessor) {
        tradeProcessorExecutorService.submit(tradeProcessor);
    }

}
