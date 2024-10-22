package io.reactivestax.factory;

import io.reactivestax.exception.InvalidMessagingTechnologyException;
import io.reactivestax.exception.InvalidPersistenceTechnologyException;
import io.reactivestax.repository.JournalEntryRepository;
import io.reactivestax.repository.LookupSecuritiesRepository;
import io.reactivestax.repository.PositionsRepository;
import io.reactivestax.repository.TradePayloadRepository;
import io.reactivestax.repository.hibernate.HibernateJournalEntryRepository;
import io.reactivestax.repository.hibernate.HibernatePositionsRepository;
import io.reactivestax.repository.hibernate.HibernateSecuritiesReferenceRepository;
import io.reactivestax.repository.hibernate.HibernateTradePayloadRepository;
import io.reactivestax.repository.jdbc.JDBCJournalEntryRepository;
import io.reactivestax.repository.jdbc.JDBCPositionsRepository;
import io.reactivestax.repository.jdbc.JDBCSecuritiesReferenceRepository;
import io.reactivestax.repository.jdbc.JDBCTradePayloadRepository;
import io.reactivestax.util.ApplicationPropertiesUtils;
import io.reactivestax.util.database.TransactionUtil;
import io.reactivestax.util.database.hibernate.HibernateTransactionUtil;
import io.reactivestax.util.database.jdbc.JDBCTransactionUtil;
import io.reactivestax.util.messaging.MessageReceiver;
import io.reactivestax.util.messaging.MessageSender;
import io.reactivestax.util.messaging.TransactionRetryer;
import io.reactivestax.util.messaging.inmemory.InMemoryMessageReceiver;
import io.reactivestax.util.messaging.inmemory.InMemoryMessageSender;
import io.reactivestax.util.messaging.inmemory.InMemoryRetry;
import io.reactivestax.util.messaging.rabbitmq.RabbitMQMessageReceiver;
import io.reactivestax.util.messaging.rabbitmq.RabbitMQMessageSender;
import io.reactivestax.util.messaging.rabbitmq.RabbitMQRetry;

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
