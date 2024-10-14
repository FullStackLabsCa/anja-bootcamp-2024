package io.reactivestax.repository;

import io.reactivestax.entity.SecuritiesReference;
import org.hibernate.Session;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class SecuritiesReferenceRepository implements LookupSecurities {

    @Override
    public boolean lookupSecurities(String cusip, Session session) {
        List<SecuritiesReference> cusipList = session.createQuery("from SecuritiesReference sr where sr.cusip = :cusip",
                SecuritiesReference.class).setParameter("cusip", cusip).getResultList();
        return !cusipList.isEmpty();
    }
}
