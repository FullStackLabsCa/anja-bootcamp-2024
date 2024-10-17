package io.reactivestax.utility.messaging.rabbitmq;

import io.reactivestax.utility.ApplicationPropertiesUtils;
import io.reactivestax.utility.database.hibernate.HibernateTransactionUtil;
import io.reactivestax.utility.messaging.QueueMessageSender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class RabbitMQQueueMessageSender implements QueueMessageSender {
    private static final Logger LOGGER = LoggerFactory.getLogger(RabbitMQQueueMessageSender.class);

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
            LOGGER.debug("sending message " + message);
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
