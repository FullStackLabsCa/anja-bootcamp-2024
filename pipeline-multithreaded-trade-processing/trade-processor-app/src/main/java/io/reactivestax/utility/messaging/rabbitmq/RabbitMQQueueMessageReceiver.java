package io.reactivestax.utility.messaging.rabbitmq;

import com.rabbitmq.client.Channel;
import io.reactivestax.utility.ApplicationPropertiesUtils;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class RabbitMQQueueMessageReceiver {
    public Channel getReceiverChannel(String queueName) throws IOException, TimeoutException {
        Channel channel = RabbitMQChannelProvider.getRabbitMQChannel();
        channel.queueDeclare(queueName, true, false, false, null);
        channel.queueBind(queueName, ApplicationPropertiesUtils.getInstance().getQueueExchangeName(), queueName);
        return channel;
    }
}
