package io.reactivestax.utility.messaging;

public interface MessageSender {
    Boolean sendMessage(String queueName, String message);
}
