package io.reactivestax.util.messaging;

public interface MessageReceiver {
    String receiveMessage(String queueName);
}

