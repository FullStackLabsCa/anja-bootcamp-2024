package io.reactivestax;

import io.reactivestax.repository.TradePayloadRepository;
import io.reactivestax.type.dto.TradePayload;
import io.reactivestax.util.ApplicationPropertiesUtils;
import io.reactivestax.util.database.ConnectionUtil;
import io.reactivestax.util.database.TransactionUtil;
import io.reactivestax.util.database.hibernate.HibernateTransactionUtil;
import io.reactivestax.util.factory.BeanFactory;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaDelete;
import org.hibernate.Session;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.List;
import java.util.logging.Logger;

public class ProducerHibernateTest {
    TradePayloadRepository tradePayloadRepository;
    ConnectionUtil<Session> connectionUtil;
    TransactionUtil transactionUtil;
    ApplicationPropertiesUtils applicationPropertiesUtils;
    Logger logger = Logger.getLogger(ProducerHibernateTest.class.getName());

    @Before
    public void setUp() {
        applicationPropertiesUtils = ApplicationPropertiesUtils.getInstance("applicationHibernateTest.properties");
        applicationPropertiesUtils.loadApplicationProperties("applicationHibernateTest.properties");
        connectionUtil = HibernateTransactionUtil.getInstance();
        tradePayloadRepository = BeanFactory.getTradePayloadRepository();
        transactionUtil = BeanFactory.getTransactionUtil();
    }

    @After
    public void cleanUp() {
        Session session = connectionUtil.getConnection();
        session.beginTransaction();
        CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
        CriteriaDelete<io.reactivestax.repository.hibernate.entity.TradePayload> criteriaDelete =
                criteriaBuilder.createCriteriaDelete(io.reactivestax.repository.hibernate.entity.TradePayload.class);
        session.createMutationQuery(criteriaDelete).executeUpdate();
        session.getTransaction().commit();
    }

    @Test
    public void testInsertRawPayloadWithTwoDifferentRecords() {
        transactionUtil.startTransaction();
        TradePayload tradePayload1 = new TradePayload();
        tradePayload1.setPayload("TDB_00000001,2024-09-25 06:58:37,TDB_CUST_2517563,TSLA,SELL,45,1480.82");
        tradePayload1.setTradeNumber("TDB_00000001");
        tradePayload1.setValidityStatus("VALID");
        tradePayloadRepository.insertTradeRawPayload(tradePayload1);
        transactionUtil.commitTransaction();
        transactionUtil.startTransaction();
        TradePayload tradePayload2 = new TradePayload();
        tradePayload2.setPayload("TDB_00000002,2024-09-25 06:58:37,TDB_CUST_2517563,V,SELL,45,1480.82");
        tradePayload2.setTradeNumber("TDB_00000002");
        tradePayload2.setValidityStatus("INVALID");
        tradePayloadRepository.insertTradeRawPayload(tradePayload2);
        transactionUtil.commitTransaction();
        Session session = connectionUtil.getConnection();
        List<io.reactivestax.repository.hibernate.entity.TradePayload> tradePayloadList = session.createQuery("from " +
                        "TradePayload",
                io.reactivestax.repository.hibernate.entity.TradePayload.class).getResultList();
        Assert.assertEquals(2, tradePayloadList.size());
    }

    @Test
    public void testInsertRawPayloadWithTwoSameRecords() {
        TradePayload tradePayload1 = new TradePayload();
        tradePayload1.setPayload("TDB_00000001,2024-09-25 06:58:37,TDB_CUST_2517563,TSLA,SELL,45,1480.82");
        tradePayload1.setTradeNumber("TDB_00000001");
        tradePayload1.setValidityStatus("VALID");
        transactionUtil.startTransaction();
        tradePayloadRepository.insertTradeRawPayload(tradePayload1);
        transactionUtil.commitTransaction();
        transactionUtil.startTransaction();
        TradePayload tradePayload2 = new TradePayload();
        tradePayload2.setPayload("TDB_00000001,2024-09-25 06:58:37,TDB_CUST_2517563,TSLA,SELL,45,1480.82");
        tradePayload2.setTradeNumber("TDB_00000001");
        tradePayload2.setValidityStatus("VALID");
        try {
            tradePayloadRepository.insertTradeRawPayload(tradePayload2);
        } catch (Exception e) {
            logger.warning("Insertion failed.");
        }
        transactionUtil.commitTransaction();
        Session session = connectionUtil.getConnection();
        List<io.reactivestax.repository.hibernate.entity.TradePayload> tradePayloadList = session.createQuery("from " +
                        "TradePayload",
                io.reactivestax.repository.hibernate.entity.TradePayload.class).getResultList();
        Assert.assertEquals(1, tradePayloadList.size());
    }
}
