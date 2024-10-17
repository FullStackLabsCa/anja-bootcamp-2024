package io.reactivestax.utility.database.hibernate;

import io.reactivestax.utility.database.ConnectionUtil;
import io.reactivestax.utility.database.TransactionUtil;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.resource.transaction.spi.TransactionStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.reactivestax.entity.JournalEntry;
import io.reactivestax.entity.Position;
import io.reactivestax.entity.SecuritiesReference;
import io.reactivestax.entity.TradePayload;

public class HibernateTransactionUtil implements TransactionUtil, ConnectionUtil<Session> {
    private static final String DEFAULT_RESOURCE = "hibernate.cfg.xml";
    private static final Logger LOGGER = LoggerFactory.getLogger(HibernateTransactionUtil.class);
    private static final ThreadLocal<Session> threadLocalSession = new ThreadLocal<>();

    private static HibernateTransactionUtil instance;
    private static SessionFactory sessionFactory;

    private HibernateTransactionUtil() {
        // private constructor to prevent instantiation
    }

    public static synchronized HibernateTransactionUtil getInstance() {
        if (instance == null) {
            instance = new HibernateTransactionUtil();
        }
        return instance;
    }

    private static synchronized SessionFactory buildSessionFactory(String resource) {
        if (sessionFactory == null) {
            try {
                // Create the SessionFactory from hibernate-annotation.cfg.xml
                Configuration configuration = new Configuration();
                configuration.addAnnotatedClass(JournalEntry.class);
                configuration.addAnnotatedClass(Position.class);
                configuration.addAnnotatedClass(SecuritiesReference.class);
                configuration.addAnnotatedClass(TradePayload.class);
                configuration.configure(resource);
                LOGGER.debug("Hibernate Annotation Configuration loaded");

                StandardServiceRegistry serviceRegistry = new StandardServiceRegistryBuilder()
                        .applySettings(configuration.getProperties())
                        .build();
                LOGGER.debug("Hibernate Annotation serviceRegistry created");

                sessionFactory = configuration.buildSessionFactory(serviceRegistry);
            } catch (Throwable ex) {
                LOGGER.error("Initial SessionFactory creation failed.", ex);
                throw new ExceptionInInitializerError(ex);
            }
        }
        return sessionFactory;
    }

    public static SessionFactory getSessionFactory() {
        return buildSessionFactory(DEFAULT_RESOURCE);
    }

    public static SessionFactory getSessionFactory(String resource) {
        return buildSessionFactory(resource);
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
//        TransactionStatus status = getConnection().getTransaction().getStatus();
//        if(status !=TransactionStatus.ACTIVE){
//            getConnection().beginTransaction();
//        }else{
//             getConnection().getTransaction();
//        }
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
