package io.reactivestax.repository;

import java.sql.Connection;
import java.sql.SQLException;

public interface LookupSecurities {
    boolean lookupSecurities(String cusip, Connection connection) throws SQLException;
}
