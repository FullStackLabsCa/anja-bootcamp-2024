package io.reactivestax.consumer.util.messaging;

public interface MessageSender {
    void sendMessage(String queueName, String message);
}
