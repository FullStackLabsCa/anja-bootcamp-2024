package io.reactivestax;

import io.reactivestax.repository.TradePayloadRepository;
import io.reactivestax.service.TradeService;
import io.reactivestax.util.ApplicationPropertiesUtils;
import io.reactivestax.util.database.ConnectionUtil;
import io.reactivestax.util.database.TransactionUtil;
import io.reactivestax.util.database.hibernate.HibernateTransactionUtil;
import io.reactivestax.util.factory.BeanFactory;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaDelete;
import org.hibernate.Session;
import org.junit.After;
import org.junit.Before;

import java.util.logging.Logger;

public class ConsumerHibernateTest {
    TradePayloadRepository tradePayloadRepository;
    ConnectionUtil<Session> connectionUtil;
    TransactionUtil transactionUtil;
    ApplicationPropertiesUtils applicationPropertiesUtils;
    TradeService tradeService;
    Logger logger = Logger.getLogger(ConsumerHibernateTest.class.getName());

    @Before
    public void setUp() {
        applicationPropertiesUtils = ApplicationPropertiesUtils.getInstance("applicationHibernateTest.properties");
        applicationPropertiesUtils.loadApplicationProperties("applicationHibernateTest.properties");
        connectionUtil = HibernateTransactionUtil.getInstance();
        tradePayloadRepository = BeanFactory.getTradePayloadRepository();
        transactionUtil = BeanFactory.getTransactionUtil();
        tradeService = TradeService.getInstance();
    }

    @After
    public void cleanUp() {
        Session session = connectionUtil.getConnection();
        session.beginTransaction();
        CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
        CriteriaDelete<io.reactivestax.repository.hibernate.entity.TradePayload> criteriaDeleteTradePayload =
                criteriaBuilder.createCriteriaDelete(io.reactivestax.repository.hibernate.entity.TradePayload.class);
        session.createMutationQuery(criteriaDeleteTradePayload).executeUpdate();
        CriteriaDelete<io.reactivestax.repository.hibernate.entity.TradePayload> criteriaDeleteJournalEntry =
                criteriaBuilder.createCriteriaDelete(io.reactivestax.repository.hibernate.entity.TradePayload.class);
        session.createMutationQuery(criteriaDeleteJournalEntry).executeUpdate();
        CriteriaDelete<io.reactivestax.repository.hibernate.entity.TradePayload> criteriaDeletePositions =
                criteriaBuilder.createCriteriaDelete(io.reactivestax.repository.hibernate.entity.TradePayload.class);
        session.createMutationQuery(criteriaDeletePositions).executeUpdate();
        session.getTransaction().commit();
    }
}
