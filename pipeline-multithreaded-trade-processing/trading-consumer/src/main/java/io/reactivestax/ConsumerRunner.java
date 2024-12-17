//package io.reactivestax;
//
//import io.reactivestax.service.TradeService;
//import io.reactivestax.util.ApplicationPropertiesUtils;
//
//import java.util.logging.Logger;
//
//public class ConsumerRunner {
//    static Logger logger = Logger.getLogger(ConsumerRunner.class.getName());
//
//    public void start() {
//        logger.info("Started trade consumer project.");
//        String rabbitmq = "rabbitmq";
//        if (ApplicationPropertiesUtils.getInstance().getMessagingTechnology().equals(rabbitmq)) {
//            TradeService.getInstance().startTradeConsumer();
//        } else {
//            logger.warning("Invalid messaging technology");
//        }
//    }
//
//    public static void main(String[] args) {
//        ConsumerRunner runner = new ConsumerRunner();
//        runner.start();
//    }
//}
