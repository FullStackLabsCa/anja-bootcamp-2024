package io.reactivestax.domainmodel.util;

import io.reactivestax.domainmodel.repository.entity.Rule;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

public class DbUtil {
    private static DbUtil instance;
    private SessionFactory sessionFactory;
    private final ThreadLocal<Session> sessionThreadLocal = new ThreadLocal<>();

    private DbUtil() {
    }

    public static synchronized DbUtil getInstance() {
        if (instance == null) {
            instance = new DbUtil();
        }

        return instance;
    }

    private synchronized SessionFactory getSessionFactory() {
        if (sessionFactory == null) {
            Configuration configuration = new Configuration()
                    .addAnnotatedClass(Rule.class)
                    .configure("hibernate.cfg.xml");
            sessionFactory = configuration.buildSessionFactory();
        }
        return sessionFactory;
    }

    public Session getConnection() {
        Session session = sessionThreadLocal.get();
        if (session == null) {
            sessionThreadLocal.set(getSessionFactory().openSession());
        }

        return sessionThreadLocal.get();
    }

    public void startTransaction() {
        Session session = sessionThreadLocal.get();
        if (session == null) {
            sessionThreadLocal.set(getSessionFactory().openSession());
        }
        sessionThreadLocal.get().beginTransaction();
    }

    public void commitTransaction() {
        Session session = sessionThreadLocal.get();
        if (session != null) {
            session.getTransaction().commit();
            session.close();
            sessionThreadLocal.remove();
        }
    }

    public void rollbackTransaction() {
        Session session = sessionThreadLocal.get();
        if (session != null) {
            session.getTransaction().rollback();
            session.close();
            sessionThreadLocal.remove();
        }
    }
}
