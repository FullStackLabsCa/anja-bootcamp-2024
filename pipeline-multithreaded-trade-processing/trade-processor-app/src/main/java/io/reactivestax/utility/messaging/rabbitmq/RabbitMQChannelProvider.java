package io.reactivestax.utility.messaging.rabbitmq;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import io.reactivestax.utility.ApplicationPropertiesUtils;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class RabbitMQChannelProvider {
    private static final ThreadLocal<Channel> channelThreadLocal = new ThreadLocal<>();

    public static synchronized Channel getRabbitMQChannel() throws IOException, TimeoutException {
        if (channelThreadLocal.get() == null) {
            ConnectionFactory connectionFactory = new ConnectionFactory();
            connectionFactory.setHost(ApplicationPropertiesUtils.getInstance().getQueueHost());
            connectionFactory.setUsername(ApplicationPropertiesUtils.getInstance().getQueueUsername());
            connectionFactory.setPassword(ApplicationPropertiesUtils.getInstance().getQueuePassword());
            Connection connection = connectionFactory.newConnection();
            Channel localChannel = connection.createChannel();
            localChannel.exchangeDeclare(ApplicationPropertiesUtils.getInstance().getQueueExchangeName(),
                    ApplicationPropertiesUtils.getInstance().getQueueExchangeType());
            channelThreadLocal.set(localChannel);
        }
        return channelThreadLocal.get();
    }
}
