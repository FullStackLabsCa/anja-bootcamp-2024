package io.reactivestax.util.database.hibernate;

import io.reactivestax.util.ApplicationPropertiesUtils;
import io.reactivestax.util.database.hibernate.HibernateTransactionUtil;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.jupiter.api.Assertions.*;

class HibernateTransactionUtilTest {
    private HibernateTransactionUtil hibernateTransactionUtil;

    @BeforeEach
    void setUp() {
        hibernateTransactionUtil = HibernateTransactionUtil.getInstance();
        ApplicationPropertiesUtils applicationPropertiesUtils = ApplicationPropertiesUtils.getInstance("applicationHibernateTest.properties");
        applicationPropertiesUtils.loadApplicationProperties("applicationHibernateTest.properties");
    }

    @Test
    void testGetSessionFactorySingleThreaded() {
        SessionFactory sessionFactory1 = HibernateTransactionUtil.getSessionFactory();
        assertNotNull(sessionFactory1);
        assertFalse(sessionFactory1.getProperties().isEmpty());
        SessionFactory sessionFactory2 = HibernateTransactionUtil.getSessionFactory();
        assertEquals(sessionFactory1, sessionFactory2);
        assertEquals(sessionFactory1.hashCode(), sessionFactory2.hashCode());
    }

    @Test
    void testGetSessionFactoryMultiThreaded() throws InterruptedException, ExecutionException {
        ExecutorService executorService = Executors.newFixedThreadPool(2);
        Callable<SessionFactory> sessionFactoryCallable = HibernateTransactionUtil::getSessionFactory;
        SessionFactory sessionFactory1 = executorService.submit(sessionFactoryCallable).get();
        SessionFactory sessionFactory2 =
                executorService.submit(sessionFactoryCallable).get();
        assertEquals(sessionFactory1, sessionFactory2);
        assertEquals(sessionFactory1.hashCode(), sessionFactory2.hashCode());
    }

    @Test
    void testGetConnectionSingleThreaded() {
        Session session = hibernateTransactionUtil.getConnection();
        assertNotNull(session);
        assertFalse(session.getTransaction().isActive());
    }

    @Test
    void testGetConnectionMultiThreaded() throws ExecutionException, InterruptedException {
        ExecutorService executorService = Executors.newFixedThreadPool(2);
        Callable<Session> sessionCallable = () -> hibernateTransactionUtil.getConnection();
        Session session1 = executorService.submit(sessionCallable).get();
        Session session2 = executorService.submit(sessionCallable).get();
        assertNotEquals(session1, session2);
        assertNotEquals(session1.hashCode(), session2.hashCode());
    }

    @Test
    void testStartTransaction() {
        hibernateTransactionUtil.startTransaction();
        Session session = hibernateTransactionUtil.getConnection();
        assertTrue(session.getTransaction().isActive());
        hibernateTransactionUtil.rollbackTransaction();
        assertFalse(session.isOpen());
    }

    @Test
    void testCommitTransaction() {
        hibernateTransactionUtil.startTransaction();
        Session session = hibernateTransactionUtil.getConnection();
        hibernateTransactionUtil.commitTransaction();
        assertEquals("COMMITTED", session.getTransaction().getStatus().toString());
        assertFalse(session.getTransaction().isActive());
        assertFalse(session.isOpen());
    }

    @Test
    void testRollbackTransaction() {
        hibernateTransactionUtil.startTransaction();
        Session session = hibernateTransactionUtil.getConnection();
        hibernateTransactionUtil.rollbackTransaction();
        assertEquals("ROLLED_BACK", session.getTransaction().getStatus().toString());
        assertFalse(session.getTransaction().isActive());
        assertFalse(session.isOpen());
    }
}
