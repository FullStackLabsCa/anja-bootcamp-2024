package io.reactivestax.repository.hibernate;

import io.reactivestax.entity.SecuritiesReference;
import io.reactivestax.repository.LookupSecuritiesRepository;
import org.hibernate.Session;

import java.util.List;

public class HibernateSecuritiesRepositoryReferenceRepository implements LookupSecuritiesRepository {

    @Override
    public boolean lookupSecurities(String cusip, Session session) {
        List<SecuritiesReference> cusipList = session.createQuery("from SecuritiesReference sr where sr.cusip = :cusip",
                SecuritiesReference.class).setParameter("cusip", cusip).getResultList();
        return !cusipList.isEmpty();
    }
}