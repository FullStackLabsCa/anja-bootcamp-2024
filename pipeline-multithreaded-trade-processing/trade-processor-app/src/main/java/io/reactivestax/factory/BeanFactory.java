package io.reactivestax.factory;

import io.reactivestax.exceptions.InvalidPersistenceTechnologyException;
import io.reactivestax.repository.TradePayloadRepository;
import io.reactivestax.repository.hibernate.HibernateTradePayloadRepository;
import io.reactivestax.repository.jdbc.JDBCTradePayloadRepository;
import io.reactivestax.utility.ApplicationPropertiesUtils;
import io.reactivestax.utility.database.TransactionUtil;
import io.reactivestax.utility.database.hibernate.HibernateTransactionUtil;
import io.reactivestax.utility.database.jdbc.JDBCTransactionUtil;
import io.reactivestax.utility.messaging.QueueMessageSender;
import io.reactivestax.utility.messaging.inmemory.InMemoryQueueMessageSender;
import io.reactivestax.utility.messaging.rabbitmq.RabbitMQQueueMessageSender;

public class BeanFactory {

    private final static String RABBITMQ_MESSAGING_TECHNOLOGY = "rabbitmq";
    private final static String INMEMORY_MESSAGING_TECHNOLOGY = "inmemory";

    private final static String HIBERNATE_PERSISTENCE_TECHNOLOGY = "hibernate";
    private final static String JDBC_PERSISTENCE_TECHNOLOGY = "jdbc";


    public static QueueMessageSender getQueueMessageSender(){
        ApplicationPropertiesUtils applicationPropertiesUtils = ApplicationPropertiesUtils.getInstance();
        if(RABBITMQ_MESSAGING_TECHNOLOGY.equals(applicationPropertiesUtils.getMessagingTechnology())){
            return RabbitMQQueueMessageSender.getInstance();
        } else if(INMEMORY_MESSAGING_TECHNOLOGY.equals(applicationPropertiesUtils.getMessagingTechnology())){
            return InMemoryQueueMessageSender.getInstance();
        } else{
            throw new InvalidPersistenceTechnologyException("Invalid messaging technology");
        }
    }

    public static TradePayloadRepository getTradePayloadRepository() {
        ApplicationPropertiesUtils applicationPropertiesUtils = ApplicationPropertiesUtils.getInstance();
        if(HIBERNATE_PERSISTENCE_TECHNOLOGY.equals(applicationPropertiesUtils.getPersistenceTechnology())){
            return HibernateTradePayloadRepository.getInstance();
        } else if(JDBC_PERSISTENCE_TECHNOLOGY.equals(applicationPropertiesUtils.getPersistenceTechnology())){
            return JDBCTradePayloadRepository.getInstance();
        } else{
            throw new InvalidPersistenceTechnologyException("Invalid persistence technology");
        }
    }

    public static TransactionUtil getTransactionUtil() {
        ApplicationPropertiesUtils applicationPropertiesUtils = ApplicationPropertiesUtils.getInstance();
        if(HIBERNATE_PERSISTENCE_TECHNOLOGY.equals(applicationPropertiesUtils.getPersistenceTechnology())){
            return HibernateTransactionUtil.getInstance();
        } else if(JDBC_PERSISTENCE_TECHNOLOGY.equals(applicationPropertiesUtils.getPersistenceTechnology())){
            return JDBCTransactionUtil.getInstance();
        } else{
            throw new InvalidPersistenceTechnologyException("Invalid persistence technology");
        }
    }


}
