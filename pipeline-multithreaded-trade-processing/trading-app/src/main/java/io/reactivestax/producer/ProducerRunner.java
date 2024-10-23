package io.reactivestax.producer;

import io.reactivestax.producer.service.TradeService;
import io.reactivestax.producer.util.ApplicationPropertiesUtils;

import java.util.logging.Logger;

public class ProducerRunner {
    static Logger logger = Logger.getLogger(ProducerRunner.class.getName());

    public void start() {
        logger.info("Started trade producer project.");
        String rabbitmq = "rabbitmq";
        if (ApplicationPropertiesUtils.getInstance().getMessagingTechnology().equals(rabbitmq)) {
            TradeService tradeService = new TradeService();
            tradeService.startTradeProducer();
        } else{
            logger.warning("Invalid messaging technology");
        }
    }

    public static void main(String[] args) {
        ProducerRunner runner = new ProducerRunner();
        runner.start();
    }
}
