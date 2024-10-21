package io.reactivestax.utility.messaging.rabbitmq;

import com.rabbitmq.client.Channel;
import io.reactivestax.exceptions.MessageDeliveryException;
import io.reactivestax.utility.ApplicationPropertiesUtils;
import io.reactivestax.utility.messaging.MessageSender;

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
