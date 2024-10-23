package io.reactivestax.consumer;

import io.reactivestax.consumer.service.TradeService;
import io.reactivestax.consumer.util.ApplicationPropertiesUtils;

import java.util.logging.Logger;

public class ConsumerRunner {
    static Logger logger = Logger.getLogger(ConsumerRunner.class.getName());

    public void start() {
        logger.info("Started trade consumer project.");
        String rabbitmq = "rabbitmq";
        if (ApplicationPropertiesUtils.getInstance().getMessagingTechnology().equals(rabbitmq)) {
            TradeService tradeService = new TradeService();
            tradeService.startTradeConsumer();
        } else {
            logger.warning("Invalid messaging technology");
        }
    }

    public static void main(String[] args) {
        ConsumerRunner runner = new ConsumerRunner();
        runner.start();
    }
}
