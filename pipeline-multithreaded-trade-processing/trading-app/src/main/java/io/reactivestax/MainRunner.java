package io.reactivestax;

import io.reactivestax.consumer.service.TradeService;
import io.reactivestax.consumer.util.ApplicationPropertiesUtils;

import java.util.logging.Logger;

public class MainRunner {
    static Logger logger = Logger.getLogger(MainRunner.class.getName());

    public void start() {
        logger.info("Started project.");
        String producer = "producer";
        String consumer = "consumer";
        String inMemory = "in-memory";
        TradeService tradeService = new TradeService();
        if (ApplicationPropertiesUtils.getInstance().getMessagingTechnology().equals(inMemory)) {
            tradeService.startTradeProducer();
            tradeService.startTradeConsumer();
        } else {
            if (ApplicationPropertiesUtils.getInstance().getTradingAppMode().equals(producer)) {
                tradeService.startTradeProducer();
            } else if (ApplicationPropertiesUtils.getInstance().getTradingAppMode().equals(consumer)) {
                tradeService.startTradeConsumer();
            }
        }
    }

    public static void main(String[] args) {
        MainRunner runner = new MainRunner();
        runner.start();
    }
}
