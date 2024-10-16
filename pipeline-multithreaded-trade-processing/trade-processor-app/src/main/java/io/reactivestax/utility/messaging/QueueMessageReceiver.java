package io.reactivestax.utility.messaging;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public interface QueueMessageReceiver {
    String receiveMessageFromQueue(String queueName) throws IOException, TimeoutException;
}
