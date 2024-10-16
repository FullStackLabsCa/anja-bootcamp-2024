package io.reactivestax.repository.hibernate;

import io.reactivestax.entity.SecuritiesReference;
import io.reactivestax.factory.BeanFactory;
import io.reactivestax.repository.LookupSecuritiesRepository;
import io.reactivestax.repository.jdbc.JDBCSecuritiesReferenceRepository;
import io.reactivestax.utility.database.TransactionUtil;
import org.hibernate.Session;

import java.util.ArrayList;
import java.util.List;

public class HibernateSecuritiesReferenceRepository implements LookupSecuritiesRepository {
    private static HibernateSecuritiesReferenceRepository instance;

    private HibernateSecuritiesReferenceRepository() {
    }

    public static synchronized HibernateSecuritiesReferenceRepository getInstance() {
        if (instance == null) {
            instance = new HibernateSecuritiesReferenceRepository();
        }
        return instance;
    }
    @Override
    public boolean lookupSecurities(String cusip) {
        Session session = getSession();
        List<SecuritiesReference> cusipList = session.createQuery("from SecuritiesReference sr where sr.cusip = :cusip",
                SecuritiesReference.class).setParameter("cusip", cusip).getResultList();
        return !cusipList.isEmpty();
    }

    private static Session getSession() {
        TransactionUtil transactionUtil = BeanFactory.getTransactionUtil();
        Session session = (Session)transactionUtil.getConnection();
        return session;
    }
}
