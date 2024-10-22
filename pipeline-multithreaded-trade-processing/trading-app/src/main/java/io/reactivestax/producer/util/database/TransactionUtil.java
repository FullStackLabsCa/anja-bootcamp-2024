package io.reactivestax.producer.util.database;

public interface TransactionUtil {

    void startTransaction();

    void commitTransaction();

    void rollbackTransaction();
}
