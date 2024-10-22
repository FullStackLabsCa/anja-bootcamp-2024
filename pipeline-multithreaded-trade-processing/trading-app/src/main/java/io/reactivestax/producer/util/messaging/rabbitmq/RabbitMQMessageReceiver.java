package io.reactivestax.producer.util.messaging.rabbitmq;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.GetResponse;
import io.reactivestax.producer.util.messaging.MessageReceiver;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.logging.Logger;

public class RabbitMQMessageReceiver implements MessageReceiver {
    private static RabbitMQMessageReceiver instance;
    private final Logger logger = Logger.getLogger(RabbitMQMessageReceiver.class.getName());
    private final ThreadLocal<GetResponse> responseThreadLocal = new ThreadLocal<>();

    private RabbitMQMessageReceiver() {
    }

    public static synchronized RabbitMQMessageReceiver getInstance() {
        if (instance == null) {
            instance = new RabbitMQMessageReceiver();
        }

        return instance;
    }


    @Override
    public String receiveMessage(String queueName) {
        String message = "";
        Channel receiverChannel = RabbitMQChannelProvider.getInstance().getReceiverChannel(queueName);
        try {
            GetResponse response = receiverChannel.basicGet(queueName, false);
            if (response != null) {
                responseThreadLocal.set(response);
                message = new String(response.getBody(), StandardCharsets.UTF_8);
                receiverChannel.basicAck(response.getEnvelope().getDeliveryTag(), false);
            } else {
                logger.info("No message received.");
            }
        } catch (IOException e) {
            logger.warning("Error while receiving message");
        }
        return message;
    }

    public GetResponse getResponse() {
        return responseThreadLocal.get();
    }
}
