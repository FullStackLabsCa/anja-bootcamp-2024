package io.reactivestax.consumer.repository;

public interface LookupSecuritiesRepository {
    boolean lookupSecurities(String cusip);
}
