package io.reactivestax.repository;

import org.hibernate.Session;

import java.sql.Connection;
import java.sql.SQLException;

public interface LookupSecurities {
    boolean lookupSecurities(String cusip, Session session);
}
