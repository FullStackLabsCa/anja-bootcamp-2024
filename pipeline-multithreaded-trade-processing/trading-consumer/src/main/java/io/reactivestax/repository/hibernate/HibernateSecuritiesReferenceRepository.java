package io.reactivestax.repository.hibernate;

import io.reactivestax.type.entity.SecuritiesReference;
import io.reactivestax.repository.LookupSecuritiesRepository;
import io.reactivestax.util.database.hibernate.HibernateTransactionUtil;
import org.hibernate.Session;

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
        Session session = HibernateTransactionUtil.getInstance().getConnection();
        List<SecuritiesReference> cusipList = session.createQuery("from SecuritiesReference sr where sr.cusip = :cusip",
                SecuritiesReference.class).setParameter("cusip", cusip).getResultList();
        return !cusipList.isEmpty();
    }
}
