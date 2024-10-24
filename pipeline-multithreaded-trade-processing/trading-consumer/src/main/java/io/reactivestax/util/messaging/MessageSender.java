package io.reactivestax.util.messaging;

public interface MessageSender {
    void sendMessage(String queueName, String message);
}
