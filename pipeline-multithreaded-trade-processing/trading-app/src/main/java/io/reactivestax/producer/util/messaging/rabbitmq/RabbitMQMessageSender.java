package io.reactivestax.producer.util.messaging.rabbitmq;

import com.rabbitmq.client.Channel;
import io.reactivestax.producer.type.exception.MessageDeliveryException;
import io.reactivestax.producer.util.ApplicationPropertiesUtils;
import io.reactivestax.producer.util.messaging.MessageSender;

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
