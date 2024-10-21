package io.reactivestax.utility.messaging.rabbitmq;

import com.rabbitmq.client.Channel;
import io.reactivestax.utility.ApplicationPropertiesUtils;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeoutException;

public class RabbitMQMessageReceiver {
    private static final String EXCHANGE_NAME = ApplicationPropertiesUtils.getInstance().getQueueExchangeName();
    private static final int RETRY_TTL = 5;

    public Channel getReceiverChannel(String queueName) throws IOException, TimeoutException {
        String retryQueueName =
                ApplicationPropertiesUtils.getInstance().getRetryQueueName() + queueName.substring(queueName.length() - 2);
        Channel channel = RabbitMQChannelProvider.getRabbitMQChannel();
        channel.queueDeclare(ApplicationPropertiesUtils.getInstance().getDlqName(), true, false, false, null);
        Map<String, Object> retryArgs = new HashMap<>();
        retryArgs.put("x-message-ttl", RETRY_TTL);
        retryArgs.put("x-dead-letter-exchange", EXCHANGE_NAME);
        retryArgs.put("x-dead-letter-routing-key", queueName);
        channel.queueDeclare(retryQueueName, true, false, false, retryArgs);
        channel.queueBind(retryQueueName, EXCHANGE_NAME, retryQueueName);
        Map<String, Object> mainQueueArgs = new HashMap<>();
        mainQueueArgs.put("x-dead-letter-exchange", EXCHANGE_NAME);
        mainQueueArgs.put("x-dead-letter-routing-key", retryQueueName);
        channel.queueDeclare(queueName, true, false, false, mainQueueArgs);
        channel.queueBind(queueName, EXCHANGE_NAME, queueName);
        return channel;
    }
}
