package io.reactivestax.utility.messaging.inmemory;

import io.reactivestax.utility.messaging.QueueMessageSender;

public class InMemoryQueueMessageSender implements QueueMessageSender {
    @Override
    public Boolean sendMessageToQueue(String queueName, String message) {
        return null;
    }

    private static InMemoryQueueMessageSender instance;

    private InMemoryQueueMessageSender() {
    }

    public static synchronized InMemoryQueueMessageSender getInstance() {
        if (instance == null) {
            instance = new InMemoryQueueMessageSender();
        }
        return instance;
    }
}
