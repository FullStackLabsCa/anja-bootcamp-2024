package io.reactivestax.repository.hibernate;

import io.reactivestax.repository.hibernate.entity.SecuritiesReference;
import io.reactivestax.util.ApplicationPropertiesUtils;
import io.reactivestax.util.database.hibernate.HibernateTransactionUtil;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaDelete;
import org.hibernate.Session;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class HibernateSecuritiesReferenceRepositoryTest {
    private final HibernateSecuritiesReferenceRepository hibernateSecuritiesReferenceRepository = HibernateSecuritiesReferenceRepository.getInstance();
    private final HibernateTransactionUtil hibernateTransactionUtil = HibernateTransactionUtil.getInstance();

    @BeforeEach
    void setUp() {
        ApplicationPropertiesUtils applicationPropertiesUtils = ApplicationPropertiesUtils.getInstance(
                "applicationHibernateTest.properties");
        applicationPropertiesUtils.loadApplicationProperties("applicationHibernateTest.properties");
    }

    @Test
    void testLookupSecurities(){
        hibernateTransactionUtil.startTransaction();
        SecuritiesReference securitiesReference1 = new SecuritiesReference();
        securitiesReference1.setCusip("TSLA");
        Session session = hibernateTransactionUtil.getConnection();
        session.persist(securitiesReference1);
        SecuritiesReference securitiesReference2 = new SecuritiesReference();
        securitiesReference2.setCusip("AMZN");
        session.persist(securitiesReference2);
        boolean b = hibernateSecuritiesReferenceRepository.lookupSecurities("TSLA");
        assertTrue(b);
        boolean b1 = hibernateSecuritiesReferenceRepository.lookupSecurities("AMZN");
        assertTrue(b1);
        boolean b2 = hibernateSecuritiesReferenceRepository.lookupSecurities("V");
        assertFalse(b2);
        hibernateTransactionUtil.rollbackTransaction();
    }
}
