package io.reactivestax.utility.messaging;

public interface MessageReceiver {
    String receiveMessage(String queueName);
}

