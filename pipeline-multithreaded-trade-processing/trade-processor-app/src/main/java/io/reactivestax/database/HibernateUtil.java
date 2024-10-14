package io.reactivestax.database;

import io.reactivestax.entity.JournalEntry;
import io.reactivestax.entity.Position;
import io.reactivestax.entity.SecuritiesReference;
import io.reactivestax.entity.TradePayload;
import org.hibernate.HibernateException;
import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class HibernateUtil {

    private static final String DEFAULT_RESOURCE = "hibernate.cfg.xml";
    private static final Logger LOGGER = LoggerFactory.getLogger(HibernateUtil.class);

    private HibernateUtil() {
    }

    private static SessionFactory buildSessionFactory(String resource) {
        SessionFactory sessionFactory = null;
        try {
            // Create the SessionFactory from hibernate-annotation.cfg.xml
            Configuration configuration = new Configuration();
            configuration.addAnnotatedClass(TradePayload.class);
            configuration.addAnnotatedClass(JournalEntry.class);
            configuration.addAnnotatedClass(SecuritiesReference.class);
            configuration.addAnnotatedClass(Position.class);
            configuration.configure(resource);
            LOGGER.debug("Hibernate Annotation Configuration loaded");

            StandardServiceRegistry serviceRegistry = new StandardServiceRegistryBuilder()
                    .applySettings(configuration.getProperties())
                    .build();
            LOGGER.debug("Hibernate Annotation serviceRegistry created");

            sessionFactory = configuration.buildSessionFactory(serviceRegistry);
        } catch (HibernateException ex) {
            LOGGER.error("Initial SessionFactory creation failed.", ex);
        }
        return sessionFactory;
    }

    public static SessionFactory getSessionFactory() {
        return buildSessionFactory(DEFAULT_RESOURCE);
    }

    public static SessionFactory getSessionFactory(String resource) {
        return buildSessionFactory(resource);
    }
}
