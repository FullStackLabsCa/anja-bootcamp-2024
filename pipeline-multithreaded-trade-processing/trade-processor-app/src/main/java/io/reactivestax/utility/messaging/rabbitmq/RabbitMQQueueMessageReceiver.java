package io.reactivestax.utility.messaging.rabbitmq;

import com.rabbitmq.client.Channel;

public class RabbitMQQueueMessageReceiver {
    private static RabbitMQQueueMessageReceiver instance;
    private static final ThreadLocal<Channel> channelThreadLocal = new ThreadLocal<>();

    private RabbitMQQueueMessageReceiver() {
    }

    public static synchronized RabbitMQQueueMessageReceiver getInstance() {
        if (instance == null) {
            instance = new RabbitMQQueueMessageReceiver();
        }
        return instance;
    }
}
