package io.reactivestax.util.messaging.rabbitmq;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.GetResponse;

import io.reactivestax.util.ApplicationPropertiesUtils;
import io.reactivestax.util.messaging.TransactionRetryer;

public class RabbitMQRetry implements TransactionRetryer {
    private static RabbitMQRetry instance;
    Logger logger = Logger.getLogger(RabbitMQRetry.class.getName());

    private RabbitMQRetry() {
    }

    public static synchronized RabbitMQRetry getInstance() {
        if (instance == null) {
            instance = new RabbitMQRetry();
        }

        return instance;
    }

    @Override
    public void retryTradeProcessing(String tradeId, String queueName) {
        String retryHeader = "x-retry-count";
        GetResponse getResponse = RabbitMQMessageReceiver.getInstance().getResponse();
        Map<String, Object> headers = getResponse.getProps().getHeaders();
        int retryCount = (headers != null && headers.containsKey(retryHeader)) ? (int) headers.get(retryHeader) : 0;
        ApplicationPropertiesUtils applicationPropertiesUtils = ApplicationPropertiesUtils.getInstance();
        Channel channel = RabbitMQChannelProvider.getInstance().getReceiverChannel(queueName);
        try {
            if (retryCount < applicationPropertiesUtils.getMaxRetryCount()) {
                Map<String, Object> retryHeaders = new HashMap<>();
                retryHeaders.put(retryHeader, retryCount + 1);
                AMQP.BasicProperties retryProps = new AMQP.BasicProperties.Builder()
                        .headers(retryHeaders)
                        .build();
                channel.basicPublish(applicationPropertiesUtils.getQueueExchangeName(),
                        applicationPropertiesUtils.getRetryQueueName() + queueName.substring(queueName.length() - 2),
                        retryProps,
                        tradeId.getBytes(StandardCharsets.UTF_8));

            } else {
                channel.basicPublish("", applicationPropertiesUtils.getDlqName(), null, tradeId.getBytes());
            }
        } catch (IOException e) {
            logger.warning("Error while retrying transaction");
        }
    }
}
