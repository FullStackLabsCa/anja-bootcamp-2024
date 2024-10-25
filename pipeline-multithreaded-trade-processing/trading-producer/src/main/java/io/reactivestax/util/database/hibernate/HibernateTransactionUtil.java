package io.reactivestax.util.database.hibernate;

import io.reactivestax.repository.hibernate.entity.TradePayload;
import io.reactivestax.util.ApplicationPropertiesUtils;
import io.reactivestax.util.database.ConnectionUtil;
import io.reactivestax.util.database.TransactionUtil;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HibernateTransactionUtil implements TransactionUtil, ConnectionUtil<Session> {
    private static final String DEFAULT_RESOURCE = "hibernate.cfg.xml";
    private static final Logger LOGGER = LoggerFactory.getLogger(HibernateTransactionUtil.class);
    private final ThreadLocal<Session> threadLocalSession = new ThreadLocal<>();
    private static HibernateTransactionUtil instance;
    private static SessionFactory sessionFactory;
    private static final ApplicationPropertiesUtils applicationPropertiesUtils = ApplicationPropertiesUtils.getInstance();

    private HibernateTransactionUtil() {
    }

    public static synchronized HibernateTransactionUtil getInstance() {
        if (instance == null) {
            instance = new HibernateTransactionUtil();
        }
        return instance;
    }

    private static synchronized SessionFactory buildSessionFactory(String resource) {
        if (sessionFactory == null) {
            Configuration configuration = getConfiguration();
            configuration.configure(resource);
            LOGGER.debug("Hibernate Annotation Configuration loaded");

            StandardServiceRegistry serviceRegistry = new StandardServiceRegistryBuilder()
                    .applySettings(configuration.getProperties())
                    .build();
            LOGGER.debug("Hibernate Annotation serviceRegistry created");

            sessionFactory = configuration.buildSessionFactory(serviceRegistry);
        }
        return sessionFactory;
    }

    private static Configuration getConfiguration() {
        Configuration configuration = new Configuration();
        configuration.setProperty("hibernate.connection.driver_class", applicationPropertiesUtils.getDbDriverClass());
        configuration.setProperty("hibernate.connection.url", applicationPropertiesUtils.getDbUrl());
        configuration.setProperty("hibernate.connection.username", applicationPropertiesUtils.getDbUsername());
        configuration.setProperty("hibernate.connection.password", applicationPropertiesUtils.getDbPassword());
        configuration.setProperty("hibernate.dialect", applicationPropertiesUtils.getHibernateDialect());
        configuration.setProperty("hibernate.hbm2ddl.auto", applicationPropertiesUtils.getHibernateDBCreationMode());
        configuration.addAnnotatedClass(TradePayload.class);
        return configuration;
    }

    public static SessionFactory getSessionFactory() {
        return buildSessionFactory(DEFAULT_RESOURCE);
    }

    @Override
    public Session getConnection() {
        Session session = threadLocalSession.get();
        if (session == null) {
            session = getSessionFactory().openSession();
            threadLocalSession.set(session);
        }
        return session;
    }

    @Override
    public void startTransaction() {
        getConnection().beginTransaction();
    }

    private void closeConnection() {
        Session session = threadLocalSession.get();
        if (session != null) {
            session.close();
            threadLocalSession.remove();
        }
    }

    @Override
    public void commitTransaction() {
        getConnection().getTransaction().commit();
        closeConnection();
    }

    @Override
    public void rollbackTransaction() {
        getConnection().getTransaction().rollback();
        closeConnection();
    }
}
