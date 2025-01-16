package io.reactivestax.tradingproducer;

import io.reactivestax.tradingproducer.configuration.AppConfig;
import io.reactivestax.tradingproducer.service.TradeService;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.util.logging.Logger;

public class ProducerRunner {
    static Logger logger = Logger.getLogger(ProducerRunner.class.getName());

    public static void main(String[] args) {
        ApplicationContext context = new AnnotationConfigApplicationContext(AppConfig.class);
        TradeService tradeService = context.getBean(TradeService.class);
        tradeService.startTradeProducer();
    }
}
