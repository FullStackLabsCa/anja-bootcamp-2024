package io.reactivestax.util.factory;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;

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
import io.reactivestax.type.Constants;
import io.reactivestax.type.exception.InvalidMessagingTechnologyException;
import io.reactivestax.type.exception.InvalidPersistenceTechnologyException;
import io.reactivestax.util.ApplicationPropertiesUtils;
import io.reactivestax.util.database.TransactionUtil;
import io.reactivestax.util.database.hibernate.HibernateTransactionUtil;
import io.reactivestax.util.database.jdbc.JDBCTransactionUtil;
import io.reactivestax.util.messaging.MessageReceiver;
import io.reactivestax.util.messaging.TransactionRetryer;
import io.reactivestax.util.messaging.rabbitmq.RabbitMQMessageReceiver;
import io.reactivestax.util.messaging.rabbitmq.RabbitMQRetry;

public class BeanFactory {

    private BeanFactory() {
    }

    public static TransactionUtil getTransactionUtil() {
        ApplicationPropertiesUtils applicationPropertiesUtils = ApplicationPropertiesUtils.getInstance();

        // AFTER
        Map<String, Supplier<TransactionUtil>> transactionUtilMap = new HashMap<>();
        transactionUtilMap.put(Constants.HIBERNATE_PERSISTENCE_TECHNOLOGY, HibernateTransactionUtil::getInstance);
        transactionUtilMap.put(Constants.JDBC_PERSISTENCE_TECHNOLOGY, JDBCTransactionUtil::getInstance);

        Optional<String> optionalPersistenceTechnology = Optional
                .ofNullable(applicationPropertiesUtils.getPersistenceTechnology());
        return optionalPersistenceTechnology
                .map(transactionUtilMap::get)
                .map(Supplier::get)
                .orElseThrow(InvalidPersistenceTechnologyException::new);

        // BEFORE
        // if
        // (Constants.HIBERNATE_PERSISTENCE_TECHNOLOGY.equals(applicationPropertiesUtils.getPersistenceTechnology()))
        // {
        // return HibernateTransactionUtil.getInstance();
        // } else if (Constants.JDBC_PERSISTENCE_TECHNOLOGY
        // .equals(applicationPropertiesUtils.getPersistenceTechnology())) {
        // return JDBCTransactionUtil.getInstance();
        // } else {
        // throw new
        // InvalidPersistenceTechnologyException(Constants.INVALID_PERSISTENCE_TECHNOLOGY);
        // }
    }

    public static TradePayloadRepository getTradePayloadRepository() {
        ApplicationPropertiesUtils applicationPropertiesUtils = ApplicationPropertiesUtils.getInstance();
        if (Constants.HIBERNATE_PERSISTENCE_TECHNOLOGY.equals(applicationPropertiesUtils.getPersistenceTechnology())) {
            return HibernateTradePayloadRepository.getInstance();
        } else if (Constants.JDBC_PERSISTENCE_TECHNOLOGY
                .equals(applicationPropertiesUtils.getPersistenceTechnology())) {
            return JDBCTradePayloadRepository.getInstance();
        } else {
            throw new InvalidPersistenceTechnologyException(Constants.INVALID_PERSISTENCE_TECHNOLOGY);
        }
    }

    public static LookupSecuritiesRepository getLookupSecuritiesRepository() {
        ApplicationPropertiesUtils applicationPropertiesUtils = ApplicationPropertiesUtils.getInstance();
        if (Constants.HIBERNATE_PERSISTENCE_TECHNOLOGY.equals(applicationPropertiesUtils.getPersistenceTechnology())) {
            return HibernateSecuritiesReferenceRepository.getInstance();
        } else if (Constants.JDBC_PERSISTENCE_TECHNOLOGY
                .equals(applicationPropertiesUtils.getPersistenceTechnology())) {
            return JDBCSecuritiesReferenceRepository.getInstance();
        } else {
            throw new InvalidPersistenceTechnologyException(Constants.INVALID_PERSISTENCE_TECHNOLOGY);
        }
    }

    public static JournalEntryRepository getJournalEntryRepository() {
        ApplicationPropertiesUtils applicationPropertiesUtils = ApplicationPropertiesUtils.getInstance();
        if (Constants.HIBERNATE_PERSISTENCE_TECHNOLOGY.equals(applicationPropertiesUtils.getPersistenceTechnology())) {
            return HibernateJournalEntryRepository.getInstance();
        } else if (Constants.JDBC_PERSISTENCE_TECHNOLOGY
                .equals(applicationPropertiesUtils.getPersistenceTechnology())) {
            return JDBCJournalEntryRepository.getInstance();
        } else {
            throw new InvalidPersistenceTechnologyException(Constants.INVALID_PERSISTENCE_TECHNOLOGY);
        }
    }

    public static PositionsRepository getPositionsRepository() {
        ApplicationPropertiesUtils applicationPropertiesUtils = ApplicationPropertiesUtils.getInstance();
        if (Constants.HIBERNATE_PERSISTENCE_TECHNOLOGY.equals(applicationPropertiesUtils.getPersistenceTechnology())) {
            return HibernatePositionsRepository.getInstance();
        } else if (Constants.JDBC_PERSISTENCE_TECHNOLOGY
                .equals(applicationPropertiesUtils.getPersistenceTechnology())) {
            return JDBCPositionsRepository.getInstance();
        } else {
            throw new InvalidPersistenceTechnologyException(Constants.INVALID_PERSISTENCE_TECHNOLOGY);
        }
    }

    public static MessageReceiver getMessageReceiver() {
        ApplicationPropertiesUtils applicationPropertiesUtils = ApplicationPropertiesUtils.getInstance();
        if (Constants.RABBITMQ_MESSAGING_TECHNOLOGY.equals(applicationPropertiesUtils.getMessagingTechnology())) {
            return RabbitMQMessageReceiver.getInstance();
        } else {
            throw new InvalidMessagingTechnologyException(Constants.INVALID_MESSAGING_TECHNOLOGY);
        }
    }

    public static TransactionRetryer getTradeProcessingRetryer() {
        ApplicationPropertiesUtils applicationPropertiesUtils = ApplicationPropertiesUtils.getInstance();
        if (Constants.RABBITMQ_MESSAGING_TECHNOLOGY.equals(applicationPropertiesUtils.getMessagingTechnology())) {
            return RabbitMQRetry.getInstance();
        } else {
            throw new InvalidMessagingTechnologyException(Constants.INVALID_MESSAGING_TECHNOLOGY);
        }
    }
}
