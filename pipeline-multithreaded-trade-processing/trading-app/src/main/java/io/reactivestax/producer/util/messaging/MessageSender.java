package io.reactivestax.producer.util.messaging;

public interface MessageSender {
    void sendMessage(String queueName, String message);
}
