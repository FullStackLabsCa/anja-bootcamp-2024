package io.reactivestax.utility.messaging.rabbitmq;

import io.reactivestax.utility.ApplicationPropertiesUtils;
import io.reactivestax.utility.messaging.QueueMessageSender;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class RabbitMQQueueMessageSender implements QueueMessageSender {
    private static RabbitMQQueueMessageSender instance;

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
        try{
            RabbitMQChannelProvider.getRabbitMQChannel().basicPublish(ApplicationPropertiesUtils.getInstance().getQueueExchangeName(), queueName, null,
                    message.getBytes());
            return true;
        }catch (Throwable e){
            e.printStackTrace();
            //TODO: Make custom exception here
            throw new RuntimeException(e);
        }
    }

}
