package io.reactivestax.utility.messaging.rabbitmq;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import io.reactivestax.repository.hibernate.HibernateTradePayloadRepository;
import io.reactivestax.utility.ApplicationPropertiesUtils;
import io.reactivestax.utility.messaging.QueueMessageSender;
import io.reactivestax.utility.rabbitmq.QueueUtil;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class RabbitMQQueueMessageSender implements QueueMessageSender {
    private static RabbitMQQueueMessageSender instance;
    private static Channel channel;

    private RabbitMQQueueMessageSender() {
    }

    public static synchronized RabbitMQQueueMessageSender getInstance() {
        if (instance == null) {
            instance = new RabbitMQQueueMessageSender();
            try {
                channel = getRabbitMQChannel();
            } catch (IOException |TimeoutException e) {
                e.printStackTrace();
                //TODO: Make custom exception here
                throw new RuntimeException(e);
            }
        }
        return instance;
    }

    @Override
    public Boolean sendMessageToQueue(String queueName, String message) throws IOException, TimeoutException {
        try{
            channel.basicPublish(ApplicationPropertiesUtils.getInstance().getQueueExchangeName(), queueName, null, message.getBytes());
            return true;
        }catch (Throwable e){
            e.printStackTrace();
            //TODO: Make custom exception here
            throw new RuntimeException(e);
        }
    }

    private static Channel getRabbitMQChannel() throws IOException, TimeoutException {
        if (channel == null) {
            ConnectionFactory connectionFactory = new ConnectionFactory();
            connectionFactory.setHost(ApplicationPropertiesUtils.getInstance().getQueueHost());
            connectionFactory.setUsername(ApplicationPropertiesUtils.getInstance().getQueueUsername());
            connectionFactory.setPassword(ApplicationPropertiesUtils.getInstance().getQueuePassword());
            Connection connection = connectionFactory.newConnection();
            Channel channel = connection.createChannel();
            channel.exchangeDeclare(ApplicationPropertiesUtils.getInstance().getQueueExchangeName(),
                    ApplicationPropertiesUtils.getInstance().getQueueExchangeType());
        }
        return channel;
    }

}
