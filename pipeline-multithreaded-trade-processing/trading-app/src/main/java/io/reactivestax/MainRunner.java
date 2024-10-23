package io.reactivestax;

import io.reactivestax.consumer.service.TradeService;

import java.util.logging.Logger;

public class MainRunner {
    static Logger logger = Logger.getLogger(MainRunner.class.getName());

    public void start() {
        logger.info("Started project.");
        TradeService tradeService = new TradeService();
        tradeService.startTradeProducer();
        tradeService.startTradeConsumer();
    }

    public static void main(String[] args) {
        MainRunner runner = new MainRunner();
        runner.start();
    }
}
