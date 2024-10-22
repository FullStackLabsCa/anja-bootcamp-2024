package io.reactivestax.consumer.util.messaging.rabbitmq;

import com.rabbitmq.client.Channel;
import io.reactivestax.consumer.type.exception.MessageDeliveryException;
import io.reactivestax.consumer.util.ApplicationPropertiesUtils;
import io.reactivestax.consumer.util.messaging.MessageSender;

import java.io.IOException;

public class RabbitMQMessageSender implements MessageSender {
    private static RabbitMQMessageSender instance;

    private RabbitMQMessageSender() {
    }

    public static synchronized RabbitMQMessageSender getInstance() {
        if (instance == null) {
            instance = new RabbitMQMessageSender();
        }
        return instance;
    }

    @Override
    public void sendMessage(String queueName, String message) {
        Channel senderChannel = RabbitMQChannelProvider.getInstance().getSenderChannel();
        try {
            senderChannel.basicPublish(ApplicationPropertiesUtils.getInstance().getQueueExchangeName(), queueName, null, message.getBytes());
        } catch (IOException e) {
            throw new MessageDeliveryException("Error while sending message");
        }
    }
}
