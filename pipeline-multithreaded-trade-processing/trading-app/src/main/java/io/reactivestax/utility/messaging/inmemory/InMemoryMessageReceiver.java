package io.reactivestax.utility.messaging.inmemory;

import io.reactivestax.utility.messaging.MessageReceiver;

import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;

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
        List<LinkedBlockingQueue<String>> tradeQueues = InMemoryQueueProvider.getInstance().getTradeQueues();
        LinkedBlockingQueue<String> tradeQueue =
                tradeQueues.get(Integer.parseInt(queueName.substring(queueName.length() - 1)));
        try {
            message = tradeQueue.take();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        return message;
    }
}
