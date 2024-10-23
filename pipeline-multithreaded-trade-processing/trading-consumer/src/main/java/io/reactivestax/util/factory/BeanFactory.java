package io.reactivestax.consumer.util.factory;

import io.reactivestax.consumer.type.exception.InvalidMessagingTechnologyException;
import io.reactivestax.consumer.type.exception.InvalidPersistenceTechnologyException;
import io.reactivestax.consumer.repository.JournalEntryRepository;
import io.reactivestax.consumer.repository.LookupSecuritiesRepository;
import io.reactivestax.consumer.repository.PositionsRepository;
import io.reactivestax.consumer.repository.TradePayloadRepository;
import io.reactivestax.consumer.repository.hibernate.HibernateJournalEntryRepository;
import io.reactivestax.consumer.repository.hibernate.HibernatePositionsRepository;
import io.reactivestax.consumer.repository.hibernate.HibernateSecuritiesReferenceRepository;
import io.reactivestax.consumer.repository.hibernate.HibernateTradePayloadRepository;
import io.reactivestax.consumer.repository.jdbc.JDBCJournalEntryRepository;
import io.reactivestax.consumer.repository.jdbc.JDBCPositionsRepository;
import io.reactivestax.consumer.repository.jdbc.JDBCSecuritiesReferenceRepository;
import io.reactivestax.consumer.repository.jdbc.JDBCTradePayloadRepository;
import io.reactivestax.consumer.util.ApplicationPropertiesUtils;
import io.reactivestax.consumer.util.database.TransactionUtil;
import io.reactivestax.consumer.util.database.hibernate.HibernateTransactionUtil;
import io.reactivestax.consumer.util.database.jdbc.JDBCTransactionUtil;
import io.reactivestax.consumer.util.messaging.MessageReceiver;
import io.reactivestax.consumer.util.messaging.MessageSender;
import io.reactivestax.consumer.util.messaging.TransactionRetryer;
import io.reactivestax.consumer.util.messaging.inmemory.InMemoryMessageReceiver;
import io.reactivestax.consumer.util.messaging.inmemory.InMemoryMessageSender;
import io.reactivestax.consumer.util.messaging.inmemory.InMemoryRetry;
import io.reactivestax.consumer.util.messaging.rabbitmq.RabbitMQMessageReceiver;
import io.reactivestax.consumer.util.messaging.rabbitmq.RabbitMQMessageSender;
import io.reactivestax.consumer.util.messaging.rabbitmq.RabbitMQRetry;

public class BeanFactory {

    private BeanFactory() {
    }

    private static final String RABBITMQ_MESSAGING_TECHNOLOGY = "rabbitmq";
    private static final String IN_MEMORY_MESSAGING_TECHNOLOGY = "in-memory";

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

    public static LookupSecuritiesRepository getLookupSecuritiesRepository() {
        ApplicationPropertiesUtils applicationPropertiesUtils = ApplicationPropertiesUtils.getInstance();
        if (HIBERNATE_PERSISTENCE_TECHNOLOGY.equals(applicationPropertiesUtils.getPersistenceTechnology())) {
            return HibernateSecuritiesReferenceRepository.getInstance();
        } else if (JDBC_PERSISTENCE_TECHNOLOGY.equals(applicationPropertiesUtils.getPersistenceTechnology())) {
            return JDBCSecuritiesReferenceRepository.getInstance();
        } else {
            throw new InvalidPersistenceTechnologyException(INVALID_PERSISTENCE_TECHNOLOGY);
        }
    }

    public static JournalEntryRepository getJournalEntryRepository() {
        ApplicationPropertiesUtils applicationPropertiesUtils = ApplicationPropertiesUtils.getInstance();
        if (HIBERNATE_PERSISTENCE_TECHNOLOGY.equals(applicationPropertiesUtils.getPersistenceTechnology())) {
            return HibernateJournalEntryRepository.getInstance();
        } else if (JDBC_PERSISTENCE_TECHNOLOGY.equals(applicationPropertiesUtils.getPersistenceTechnology())) {
            return JDBCJournalEntryRepository.getInstance();
        } else {
            throw new InvalidPersistenceTechnologyException(INVALID_PERSISTENCE_TECHNOLOGY);
        }
    }

    public static PositionsRepository getPositionsRepository() {
        ApplicationPropertiesUtils applicationPropertiesUtils = ApplicationPropertiesUtils.getInstance();
        if (HIBERNATE_PERSISTENCE_TECHNOLOGY.equals(applicationPropertiesUtils.getPersistenceTechnology())) {
            return HibernatePositionsRepository.getInstance();
        } else if (JDBC_PERSISTENCE_TECHNOLOGY.equals(applicationPropertiesUtils.getPersistenceTechnology())) {
            return JDBCPositionsRepository.getInstance();
        } else {
            throw new InvalidPersistenceTechnologyException(INVALID_PERSISTENCE_TECHNOLOGY);
        }
    }

    public static MessageSender getMessageSender() {
        ApplicationPropertiesUtils applicationPropertiesUtils = ApplicationPropertiesUtils.getInstance();
        if (RABBITMQ_MESSAGING_TECHNOLOGY.equals(applicationPropertiesUtils.getMessagingTechnology())) {
            return RabbitMQMessageSender.getInstance();
        } else if (IN_MEMORY_MESSAGING_TECHNOLOGY.equals(applicationPropertiesUtils.getMessagingTechnology())) {
            return InMemoryMessageSender.getInstance();
        } else {
            throw new InvalidMessagingTechnologyException(INVALID_MESSAGING_TECHNOLOGY);
        }
    }

    public static MessageReceiver getMessageReceiver() {
        ApplicationPropertiesUtils applicationPropertiesUtils = ApplicationPropertiesUtils.getInstance();
        if (RABBITMQ_MESSAGING_TECHNOLOGY.equals(applicationPropertiesUtils.getMessagingTechnology())) {
            return RabbitMQMessageReceiver.getInstance();
        } else if (IN_MEMORY_MESSAGING_TECHNOLOGY.equals(applicationPropertiesUtils.getMessagingTechnology())) {
            return InMemoryMessageReceiver.getInstance();
        } else {
            throw new InvalidMessagingTechnologyException(INVALID_MESSAGING_TECHNOLOGY);
        }
    }

    public static TransactionRetryer getTransactionRetryer() {
        ApplicationPropertiesUtils applicationPropertiesUtils = ApplicationPropertiesUtils.getInstance();
        if (RABBITMQ_MESSAGING_TECHNOLOGY.equals(applicationPropertiesUtils.getMessagingTechnology())) {
            return RabbitMQRetry.getInstance();
        } else if (IN_MEMORY_MESSAGING_TECHNOLOGY.equals(applicationPropertiesUtils.getMessagingTechnology())) {
            return InMemoryRetry.getInstance();
        } else {
            throw new InvalidMessagingTechnologyException(INVALID_MESSAGING_TECHNOLOGY);
        }
    }
}
