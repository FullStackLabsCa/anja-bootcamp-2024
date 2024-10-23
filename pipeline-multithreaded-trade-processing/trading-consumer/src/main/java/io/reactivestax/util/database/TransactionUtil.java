package io.reactivestax.consumer.util.database;

public interface TransactionUtil {

    void startTransaction();

    void commitTransaction();

    void rollbackTransaction();
}
