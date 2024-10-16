package io.reactivestax.repository;

public interface LookupSecuritiesRepository {
    boolean lookupSecurities(String cusip);
}
