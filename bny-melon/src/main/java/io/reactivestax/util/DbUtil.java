package io.reactivestax.util;

import io.reactivestax.entity.RuleSet;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

public class DbUtil {
    private static SessionFactory sessionFactory;
    private static Session session;

    private static synchronized SessionFactory getSessionFactory() {
        if (sessionFactory == null) {
            Configuration configuration = new Configuration()
                    .addAnnotatedClass(RuleSet.class)
                    .configure("hibernate.cfg.xml");
            sessionFactory = configuration.buildSessionFactory();
        }
        return sessionFactory;
    }

    public static Session getConnection() {
        if (session == null) {
            session = getSessionFactory().openSession();
        }

        return session;
    }

    public static void startTransaction() {
        if (session == null) {
            session = getSessionFactory().openSession();
        }
        session.beginTransaction();
    }

    public static void commitTransaction() {
        if (session != null) {
            session.getTransaction().commit();
            session.close();
            session = null;
        }
    }

    public static void rollbackTransaction() {
        if (session != null) {
            session.getTransaction().rollback();
            session.close();
            session = null;
        }
    }
}
