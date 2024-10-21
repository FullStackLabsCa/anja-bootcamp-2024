package io.reactivestax.utility.messaging.inmemory;

import io.reactivestax.utility.messaging.MessageSender;

public class InMemoryMessageSender implements MessageSender {
    @Override
    public Boolean sendMessage(String queueName, String message) {
        return null;
    }

    private static InMemoryMessageSender instance;

    private InMemoryMessageSender() {
    }

    public static synchronized InMemoryMessageSender getInstance() {
        if (instance == null) {
            instance = new InMemoryMessageSender();
        }
        return instance;
    }
}
