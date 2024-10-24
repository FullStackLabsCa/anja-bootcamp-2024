package io.reactivestax.service;

import io.reactivestax.util.factory.BeanFactory;
import io.reactivestax.util.messaging.MessageReceiver;

import java.io.IOException;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.logging.Logger;

public class FileTradeProcessor implements Callable<Void> {
    Logger logger = Logger.getLogger(FileTradeProcessor.class.getName());
    String queueName;
    private final CountDownLatch latch = new CountDownLatch(1);

    int count = 0;

    public FileTradeProcessor(String queueName) {
        this.queueName = queueName;
    }

    @Override
    public Void call() throws InterruptedException {
        try {
            MessageReceiver messageReceiver = BeanFactory.getMessageReceiver();
            while (count == 0) {
                String tradeId = messageReceiver.receiveMessage(queueName);
                if (!tradeId.isEmpty()) {
                    TradeProcessorService.getInstance().processTrade(tradeId, queueName);
                } else {
                    logger.info("No trade ID received. Waiting for messages...");
                }
            }
        } catch (IOException | InterruptedException e) {
            logger.warning("Exception detected in Trade Processor.");
            Thread.currentThread().interrupt();
        }
        latch.await();
        return null;
    }
}
