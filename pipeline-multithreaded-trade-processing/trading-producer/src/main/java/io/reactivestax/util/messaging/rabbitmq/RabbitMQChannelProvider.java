package io.reactivestax.util.messaging.rabbitmq;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import io.reactivestax.util.ApplicationPropertiesUtils;

import java.io.IOException;
import java.util.concurrent.TimeoutException;
import java.util.logging.Logger;

public class RabbitMQChannelProvider {
    private static RabbitMQChannelProvider instance;
    private static final ThreadLocal<Channel> channelThreadLocal = new ThreadLocal<>();
    private Connection connection;
    private static final Logger logger = Logger.getLogger(RabbitMQChannelProvider.class.getName());
    ApplicationPropertiesUtils applicationPropertiesUtils = ApplicationPropertiesUtils.getInstance();

    private RabbitMQChannelProvider() {
    }

    public static synchronized RabbitMQChannelProvider getInstance() {
        if (instance == null) {
            instance = new RabbitMQChannelProvider();
        }

        return instance;
    }

    private synchronized Connection getRabbitMQConnection() throws IOException, TimeoutException {
        if (connection == null) {
            ConnectionFactory connectionFactory = new ConnectionFactory();
            connectionFactory.setUsername(applicationPropertiesUtils.getQueueUsername());
            connectionFactory.setPassword(applicationPropertiesUtils.getQueuePassword());
            connectionFactory.setHost(applicationPropertiesUtils.getQueueHost());
            connection = connectionFactory.newConnection();
        }
        return connection;
    }

    public Channel getSenderChannel() {
        if (channelThreadLocal.get() == null) {
            try {
                Channel localChannel = getRabbitMQConnection().createChannel();
                localChannel.exchangeDeclare(applicationPropertiesUtils.getQueueExchangeName(),
                        applicationPropertiesUtils.getQueueExchangeType());
                channelThreadLocal.set(localChannel);
            } catch (TimeoutException | IOException e) {
                logger.warning("Exception detected while creating channel.");
            }
        }

        return channelThreadLocal.get();
    }
}