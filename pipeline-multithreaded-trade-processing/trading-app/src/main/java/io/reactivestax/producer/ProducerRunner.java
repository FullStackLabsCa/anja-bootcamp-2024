package io.reactivestax.producer;

import io.reactivestax.consumer.service.TradeService;
import io.reactivestax.consumer.util.ApplicationPropertiesUtils;

import java.util.logging.Logger;

public class ProducerRunner {
    static Logger logger = Logger.getLogger(ProducerRunner.class.getName());

    public void start() {
        logger.info("Started trade producer project.");
        String producer = "producer";
        String inMemory = "in-memory";
        if (ApplicationPropertiesUtils.getInstance().getTradingAppMode().equals(producer) && !ApplicationPropertiesUtils.getInstance().getMessagingTechnology().equals(inMemory)) {
            io.reactivestax.consumer.service.TradeService tradeService = new TradeService();
            tradeService.startTradeProducer();
        } else{
            logger.warning("Invalid trading mode / Invalid messaging technology");
        }
    }

    public static void main(String[] args) {
        ProducerRunner runner = new ProducerRunner();
        runner.start();
    }
}
