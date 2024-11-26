package io.reactivestax.util.factory;

import io.reactivestax.repository.TradePayloadRepository;
import io.reactivestax.repository.hibernate.HibernateTradePayloadRepository;
import io.reactivestax.repository.jdbc.JDBCTradePayloadRepository;
import io.reactivestax.type.exception.InvalidMessagingTechnologyException;
import io.reactivestax.type.exception.InvalidPersistenceTechnologyException;
import io.reactivestax.util.ApplicationPropertiesUtils;
import io.reactivestax.util.Constants;
import io.reactivestax.util.database.TransactionUtil;
import io.reactivestax.util.database.hibernate.HibernateTransactionUtil;
import io.reactivestax.util.database.jdbc.JDBCTransactionUtil;
import io.reactivestax.util.messaging.MessageSender;
import io.reactivestax.util.messaging.rabbitmq.RabbitMQMessageSender;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;


public class BeanFactory {
    private static ApplicationPropertiesUtils applicationPropertiesUtils = ApplicationPropertiesUtils.getInstance();

    private BeanFactory() {
    }

    public static TransactionUtil getTransactionUtil() {
        Map<String, Supplier<TransactionUtil>> transactionUtilMap = new HashMap<>();
        transactionUtilMap.put(Constants.HIBERNATE_PERSISTENCE_TECHNOLOGY, HibernateTransactionUtil::getInstance);
        transactionUtilMap.put(Constants.JDBC_PERSISTENCE_TECHNOLOGY, JDBCTransactionUtil::getInstance);
        return Optional.ofNullable(applicationPropertiesUtils.getPersistenceTechnology())
                .map(transactionUtilMap::get)
                .map(Supplier::get)
                .orElseThrow(InvalidPersistenceTechnologyException::new);
    }

    public static TradePayloadRepository getTradePayloadRepository() {
        Map<String, Supplier<TradePayloadRepository>> transactionUtilMap = new HashMap<>();
        transactionUtilMap.put(Constants.HIBERNATE_PERSISTENCE_TECHNOLOGY, HibernateTradePayloadRepository::getInstance);
        transactionUtilMap.put(Constants.JDBC_PERSISTENCE_TECHNOLOGY, JDBCTradePayloadRepository::getInstance);
        return Optional.ofNullable(applicationPropertiesUtils.getPersistenceTechnology())
                .map(transactionUtilMap::get)
                .map(Supplier::get)
                .orElseThrow(InvalidPersistenceTechnologyException::new);
    }

    public static MessageSender getMessageSender() {
        Map<String, Supplier<MessageSender>> transactionUtilMap = new HashMap<>();
        transactionUtilMap.put(Constants.RABBITMQ_MESSAGING_TECHNOLOGY, RabbitMQMessageSender::getInstance);
        return Optional.ofNullable(applicationPropertiesUtils.getMessagingTechnology())
                .map(transactionUtilMap::get)
                .map(Supplier::get)
                .orElseThrow(InvalidMessagingTechnologyException::new);
    }
}
