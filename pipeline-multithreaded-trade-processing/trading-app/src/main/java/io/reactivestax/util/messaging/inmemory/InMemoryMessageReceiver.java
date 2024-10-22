package io.reactivestax.util.messaging.inmemory;

import io.reactivestax.util.messaging.MessageReceiver;

import java.util.List;
import java.util.concurrent.LinkedBlockingDeque;

public class InMemoryMessageReceiver implements MessageReceiver {
    private static InMemoryMessageReceiver instance;

    private InMemoryMessageReceiver() {
    }

    public static synchronized InMemoryMessageReceiver getInstance() {
        if (instance == null) {
            instance = new InMemoryMessageReceiver();
        }
        return instance;
    }

    @Override
    public String receiveMessage(String queueName) {
        String message = "";
        List<LinkedBlockingDeque<String>> tradeQueues = InMemoryQueueProvider.getInstance().getTradeQueues();
        LinkedBlockingDeque<String> tradeQueue =
                tradeQueues.get(Integer.parseInt(queueName.substring(queueName.length() - 1)));
        try {
            message = tradeQueue.take();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        return message;
    }
}
