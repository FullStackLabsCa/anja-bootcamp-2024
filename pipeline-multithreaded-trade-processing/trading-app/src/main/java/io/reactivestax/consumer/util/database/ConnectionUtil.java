package io.reactivestax.consumer.util.database;

public interface ConnectionUtil<T> {
    T getConnection();
}
