package io.reactivestax.utility.messaging;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public interface MessageSender {
    Boolean sendMessageToQueue(String queueName, String message) throws IOException, TimeoutException;
}
