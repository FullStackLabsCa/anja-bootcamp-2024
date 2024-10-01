package io.reactivestax.service;

import com.zaxxer.hikari.HikariDataSource;
import io.reactivestax.utility.MaintainStaticValues;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TradeProcessorService implements Submittable<TradeProcessor> {

    ExecutorService tradeProcessorExecutorService = Executors.newFixedThreadPool(MaintainStaticValues.getTradeProcessorThreadCount());
    HikariDataSource hikariDataSource;
    int queueNumber = 0;

    public void submitTrade(HikariDataSource hikariDataSource) {
        this.hikariDataSource = hikariDataSource;
        for (int i = 0; i < MaintainStaticValues.getTradeProcessorThreadCount(); i++) {
            if(queueNumber == MaintainStaticValues.getTradeProcessorQueueCount()){
                queueNumber = 0;
            }
            submitTask(new TradeProcessor(QueueDistributor.getTransactionDeque(queueNumber), hikariDataSource));
            queueNumber++;
        }
        tradeProcessorExecutorService.shutdown();
    }

    @Override
    public void submitTask(TradeProcessor tradeProcessor) {
        tradeProcessorExecutorService.submit(tradeProcessor);
    }
}
