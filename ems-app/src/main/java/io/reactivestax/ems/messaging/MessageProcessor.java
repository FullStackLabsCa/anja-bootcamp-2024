package io.reactivestax.ems.messaging;

import java.util.UUID;

public interface MessageProcessor {
    void sendMessageToQueue(String queueName, UUID message);
}
