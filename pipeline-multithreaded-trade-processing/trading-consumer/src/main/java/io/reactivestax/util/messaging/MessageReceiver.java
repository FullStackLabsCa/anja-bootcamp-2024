package io.reactivestax.consumer.util.messaging;

public interface MessageReceiver {
    String receiveMessage(String queueName);
}

