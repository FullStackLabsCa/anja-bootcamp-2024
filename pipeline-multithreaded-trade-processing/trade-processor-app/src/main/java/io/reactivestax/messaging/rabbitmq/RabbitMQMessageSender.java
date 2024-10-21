package io.reactivestax.utility.messaging.rabbitmq;

import io.reactivestax.utility.ApplicationPropertiesUtils;
import io.reactivestax.utility.messaging.MessageSender;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

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
    public Boolean sendMessageToQueue(String queueName, String message) throws IOException, TimeoutException {
        try{
            System.out.println("sending message " + message);
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
