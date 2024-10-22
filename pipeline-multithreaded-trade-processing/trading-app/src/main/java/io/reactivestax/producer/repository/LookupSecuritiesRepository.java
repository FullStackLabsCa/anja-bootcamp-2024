package io.reactivestax.producer.repository;

public interface LookupSecuritiesRepository {
    boolean lookupSecurities(String cusip);
}
