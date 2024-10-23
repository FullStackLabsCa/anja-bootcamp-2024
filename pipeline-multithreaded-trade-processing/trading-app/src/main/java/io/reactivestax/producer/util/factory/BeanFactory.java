package io.reactivestax.producer.util.factory;

import io.reactivestax.producer.repository.TradePayloadRepository;
import io.reactivestax.producer.repository.hibernate.HibernateTradePayloadRepository;
import io.reactivestax.producer.repository.jdbc.JDBCTradePayloadRepository;
import io.reactivestax.producer.type.exception.InvalidMessagingTechnologyException;
import io.reactivestax.producer.type.exception.InvalidPersistenceTechnologyException;
import io.reactivestax.producer.util.ApplicationPropertiesUtils;
import io.reactivestax.producer.util.database.TransactionUtil;
import io.reactivestax.producer.util.database.hibernate.HibernateTransactionUtil;
import io.reactivestax.producer.util.database.jdbc.JDBCTransactionUtil;
import io.reactivestax.producer.util.messaging.MessageSender;
import io.reactivestax.producer.util.messaging.rabbitmq.RabbitMQMessageSender;

public class BeanFactory {

    private BeanFactory() {
    }

    private static final String RABBITMQ_MESSAGING_TECHNOLOGY = "rabbitmq";
    private static final String HIBERNATE_PERSISTENCE_TECHNOLOGY = "hibernate";
    private static final String JDBC_PERSISTENCE_TECHNOLOGY = "jdbc";
    private static final String INVALID_PERSISTENCE_TECHNOLOGY = "Invalid persistence technology.";
    private static final String INVALID_MESSAGING_TECHNOLOGY = "Invalid messaging technology.";

    public static TransactionUtil getTransactionUtil() {
        ApplicationPropertiesUtils applicationPropertiesUtils = ApplicationPropertiesUtils.getInstance();
        if (HIBERNATE_PERSISTENCE_TECHNOLOGY.equals(applicationPropertiesUtils.getPersistenceTechnology())) {
            return HibernateTransactionUtil.getInstance();
        } else if (JDBC_PERSISTENCE_TECHNOLOGY.equals(applicationPropertiesUtils.getPersistenceTechnology())) {
            return JDBCTransactionUtil.getInstance();
        } else {
            throw new InvalidPersistenceTechnologyException(INVALID_PERSISTENCE_TECHNOLOGY);
        }
    }

    public static TradePayloadRepository getTradePayloadRepository() {
        ApplicationPropertiesUtils applicationPropertiesUtils = ApplicationPropertiesUtils.getInstance();
        if (HIBERNATE_PERSISTENCE_TECHNOLOGY.equals(applicationPropertiesUtils.getPersistenceTechnology())) {
            return HibernateTradePayloadRepository.getInstance();
        } else if (JDBC_PERSISTENCE_TECHNOLOGY.equals(applicationPropertiesUtils.getPersistenceTechnology())) {
            return JDBCTradePayloadRepository.getInstance();
        } else {
            throw new InvalidPersistenceTechnologyException(INVALID_PERSISTENCE_TECHNOLOGY);
        }
    }

    public static MessageSender getMessageSender() {
        ApplicationPropertiesUtils applicationPropertiesUtils = ApplicationPropertiesUtils.getInstance();
        if (RABBITMQ_MESSAGING_TECHNOLOGY.equals(applicationPropertiesUtils.getMessagingTechnology())) {
            return RabbitMQMessageSender.getInstance();
        } else {
            throw new InvalidMessagingTechnologyException(INVALID_MESSAGING_TECHNOLOGY);
        }
    }
}
