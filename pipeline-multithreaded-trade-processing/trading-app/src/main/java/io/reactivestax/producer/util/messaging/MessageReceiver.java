package io.reactivestax.producer.util.messaging;

public interface MessageReceiver {
    String receiveMessage(String queueName);
}

