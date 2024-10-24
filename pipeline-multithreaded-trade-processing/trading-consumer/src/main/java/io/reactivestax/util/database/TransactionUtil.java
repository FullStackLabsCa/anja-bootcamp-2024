package io.reactivestax.util.database;

public interface TransactionUtil {

    void startTransaction();

    void commitTransaction();

    void rollbackTransaction();
}
