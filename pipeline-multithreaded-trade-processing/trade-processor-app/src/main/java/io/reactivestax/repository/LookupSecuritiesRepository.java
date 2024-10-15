package io.reactivestax.repository;

import org.hibernate.Session;

public interface LookupSecuritiesRepository {
    boolean lookupSecurities(String cusip, Session session);
}
