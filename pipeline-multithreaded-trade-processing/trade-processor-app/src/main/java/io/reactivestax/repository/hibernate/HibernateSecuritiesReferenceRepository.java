package io.reactivestax.repository.hibernate;

import io.reactivestax.entity.SecuritiesReference;
import io.reactivestax.repository.LookupSecuritiesRepository;

import java.util.ArrayList;
import java.util.List;

public class HibernateSecuritiesReferenceRepository implements LookupSecuritiesRepository {

    @Override
    public boolean lookupSecurities(String cusip) {
//        List<SecuritiesReference> cusipList = session.createQuery("from SecuritiesReference sr where sr.cusip = :cusip",
//                SecuritiesReference.class).setParameter("cusip", cusip).getResultList();
//        return !cusipList.isEmpty();
        return false;
    }
}
