package io.reactivestax.utility.messaging.inmemory;

import io.reactivestax.utility.messaging.MessageReceiver;

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
        return "";
    }
}
