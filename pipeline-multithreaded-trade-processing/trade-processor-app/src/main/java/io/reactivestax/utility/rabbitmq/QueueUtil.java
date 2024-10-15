package io.reactivestax.utility.rabbitmq;

import com.rabbitmq.client.ConnectionFactory;
import io.reactivestax.utility.ApplicationPropertiesUtils;

public class QueueUtil {
    private static QueueUtil instance;
    private static final Object lock = new Object();
    private final ConnectionFactory connectionFactory;

    private QueueUtil(ApplicationPropertiesUtils applicationPropertiesUtils) {
        connectionFactory = new ConnectionFactory();
        connectionFactory.setHost(applicationPropertiesUtils.getQueueHost());
        connectionFactory.setUsername(applicationPropertiesUtils.getQueueUsername());
        connectionFactory.setPassword(applicationPropertiesUtils.getQueuePassword());
    }

    public static QueueUtil getInstance(ApplicationPropertiesUtils applicationPropertiesUtils) {
        synchronized (lock) {
            if (instance == null) {
                instance = new QueueUtil(applicationPropertiesUtils);
            }
        }

        return instance;
    }

    public ConnectionFactory getQueueConnectionFactory(){
        return connectionFactory;
    }
}