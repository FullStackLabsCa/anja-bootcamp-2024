package io.reactivestax.utility.messaging.rabbitmq;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import io.reactivestax.utility.ApplicationPropertiesUtils;
import io.reactivestax.utility.messaging.QueueMessageSender;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class RabbitMQQueueMessageSender implements QueueMessageSender {
    private static RabbitMQQueueMessageSender instance;
    private static final ThreadLocal<Channel> channelThreadLocal = new ThreadLocal<>();

    private RabbitMQQueueMessageSender() {
    }

    public static synchronized RabbitMQQueueMessageSender getInstance() {
        if (instance == null) {
            instance = new RabbitMQQueueMessageSender();
        }
        return instance;
    }

    @Override
    public Boolean sendMessageToQueue(String queueName, String message) throws IOException, TimeoutException {
        try {
            getRabbitMQChannel().basicPublish(ApplicationPropertiesUtils.getInstance().getQueueExchangeName(), queueName, null,
                    message.getBytes());
            return true;
        } catch (Throwable e) {
            e.printStackTrace();
            //TODO: Make custom exception here
            throw new RuntimeException(e);
        }
    }

    private static synchronized Channel getRabbitMQChannel() throws IOException, TimeoutException {
        if (channelThreadLocal.get() == null) {
            Channel localChannel = getChannel();
            localChannel.exchangeDeclare(ApplicationPropertiesUtils.getInstance().getQueueExchangeName(),
                    ApplicationPropertiesUtils.getInstance().getQueueExchangeType());
            channelThreadLocal.set(localChannel);
        }
        return channelThreadLocal.get();
    }

    private static Channel getChannel() throws IOException, TimeoutException {
        ConnectionFactory connectionFactory = new ConnectionFactory();
        connectionFactory.setHost(ApplicationPropertiesUtils.getInstance().getQueueHost());
        connectionFactory.setUsername(ApplicationPropertiesUtils.getInstance().getQueueUsername());
        connectionFactory.setPassword(ApplicationPropertiesUtils.getInstance().getQueuePassword());
        try (Connection connection = connectionFactory.newConnection()) {
            return connection.createChannel();
        }
    }

}
