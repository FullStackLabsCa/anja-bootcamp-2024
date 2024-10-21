package io.reactivestax.utility.messaging;

public interface MessageSender {
    void sendMessage(String queueName, String message);
}
