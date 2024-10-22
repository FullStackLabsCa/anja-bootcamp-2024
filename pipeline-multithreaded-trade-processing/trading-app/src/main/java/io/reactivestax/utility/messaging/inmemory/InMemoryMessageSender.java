package io.reactivestax.utility.messaging.inmemory;

import io.reactivestax.utility.messaging.MessageSender;

import java.util.List;
import java.util.concurrent.LinkedBlockingDeque;

public class InMemoryMessageSender implements MessageSender {
    private static InMemoryMessageSender instance;

    private InMemoryMessageSender() {
    }

    public static synchronized InMemoryMessageSender getInstance() {
        if (instance == null) {
            instance = new InMemoryMessageSender();
        }
        return instance;
    }

    @Override
    public void sendMessage(String queueName, String message) {
        int queueNumber = Integer.parseInt(queueName.substring(queueName.length() - 1));
        List<LinkedBlockingDeque<String>> tradeQueues = InMemoryQueueProvider.getInstance().getTradeQueues();
        try {
            tradeQueues.get(queueNumber).put(message);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
