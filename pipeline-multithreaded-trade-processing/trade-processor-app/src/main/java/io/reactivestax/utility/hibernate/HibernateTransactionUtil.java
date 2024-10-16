package io.reactivestax.utility.hibernate;

import io.reactivestax.utility.TransactionUtil;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.reactivestax.entity.JournalEntry;
import io.reactivestax.entity.Position;
import io.reactivestax.entity.SecuritiesReference;
import io.reactivestax.entity.TradePayload;

public class HibernateTransactionUtil implements TransactionUtil<Session, Transaction> {
    private static final String DEFAULT_RESOURCE = "hibernate.cfg.xml";
    private static final Logger LOGGER = LoggerFactory.getLogger(HibernateTransactionUtil.class);
    private static final ThreadLocal<Session> threadLocalSession = new ThreadLocal<>();

    private static HibernateTransactionUtil instance;

    private HibernateTransactionUtil() {
        // private constructor to prevent instantiation
    }

    public static synchronized HibernateTransactionUtil getInstance() {
        if (instance == null) {
            instance = new HibernateTransactionUtil();
        }
        return instance;
    }


    private static SessionFactory buildSessionFactory(String resource) {
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

            return configuration.buildSessionFactory(serviceRegistry);
        } catch (Throwable ex) {
            LOGGER.error("Initial SessionFactory creation failed.", ex);
            throw new ExceptionInInitializerError(ex);
        }
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
    public Transaction startTransaction() {
        return getConnection().beginTransaction();
    }

    @Override
    public void closeConnection() {
        Session session = threadLocalSession.get();
        if (session != null) {
            session.close();
            threadLocalSession.remove();
        }
    }

    @Override
    public void commitTransaction() {
        getConnection().getTransaction().commit();
    }

    @Override
    public void rollbackTransaction() {
        getConnection().getTransaction().rollback();
    }

}
