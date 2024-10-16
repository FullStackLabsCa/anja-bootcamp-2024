package io.reactivestax.factory;

import io.reactivestax.exceptions.InvalidPersistenceTechnologyException;
import io.reactivestax.repository.TradePayloadRepository;
import io.reactivestax.repository.hibernate.HibernateTradePayloadRepository;
import io.reactivestax.repository.jdbc.JDBCTradePayloadRepository;
import io.reactivestax.utility.ApplicationPropertiesUtils;
import io.reactivestax.utility.TransactionUtil;
import io.reactivestax.utility.hibernate.HibernateTransactionUtil;
import io.reactivestax.utility.jdbc.JDBCTransactionUtil;

public class BeanFactory {
    private final static String HIBERNATE_PERSISTENCE_TECHNOLOGY = "hibernate";
    private final static String JDBC_PERSISTENCE_TECHNOLOGY = "jdbc";


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
