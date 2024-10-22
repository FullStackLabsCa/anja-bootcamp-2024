package io.reactivestax.producer.util.messaging.rabbitmq;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import io.reactivestax.producer.util.ApplicationPropertiesUtils;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
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

    private Channel getRabbitMQChannel() {
        Channel localChannel = null;
        try {
            localChannel = getRabbitMQConnection().createChannel();
            localChannel.exchangeDeclare(applicationPropertiesUtils.getQueueExchangeName(),
                    applicationPropertiesUtils.getQueueExchangeType());
            channelThreadLocal.set(localChannel);
        } catch (TimeoutException | IOException e) {
            logger.warning("Exception detected while creating channel.");
        }

        return localChannel;
    }

    public void closeChannel() {
        Channel localChannel = channelThreadLocal.get();
        if (localChannel != null) {
            channelThreadLocal.remove();
            try {
                localChannel.close();
            } catch (TimeoutException | IOException e) {
                logger.warning("Exception detected while closing channel.");
            }
        }
    }

    public Channel getSenderChannel() {
        if (channelThreadLocal.get() == null) {
            channelThreadLocal.set(getRabbitMQChannel());
        }

        return channelThreadLocal.get();
    }

    public Channel getReceiverChannel(String queueName) {
        if (channelThreadLocal.get() == null) {
            try {
                String retryQueueName =
                        applicationPropertiesUtils.getRetryQueueName() + queueName.substring(queueName.length() - 2);
                Channel channel = getRabbitMQChannel();
                if (channel != null) {
                    channel.queueDeclare(applicationPropertiesUtils.getDlqName(), true, false, false, null);
                    Map<String, Object> retryArgs = new HashMap<>();
                    retryArgs.put("x-message-ttl", applicationPropertiesUtils.getRetryTTL());
                    retryArgs.put("x-dead-letter-exchange", applicationPropertiesUtils.getQueueExchangeName());
                    retryArgs.put("x-dead-letter-routing-key", queueName);
                    channel.queueDeclare(retryQueueName, true, false, false, retryArgs);
                    channel.queueBind(retryQueueName, applicationPropertiesUtils.getQueueExchangeName(), retryQueueName);
                    Map<String, Object> mainQueueArgs = new HashMap<>();
                    mainQueueArgs.put("x-dead-letter-exchange", applicationPropertiesUtils.getQueueExchangeName());
                    mainQueueArgs.put("x-dead-letter-routing-key", retryQueueName);
                    channel.queueDeclare(queueName, true, false, false, mainQueueArgs);
                    channel.queueBind(queueName, applicationPropertiesUtils.getQueueExchangeName(), queueName);
                    channelThreadLocal.set(channel);
                }
            } catch (IOException e) {
                logger.warning("Error while getting message receiver channel.");
            }
        }

        return channelThreadLocal.get();
    }
}
