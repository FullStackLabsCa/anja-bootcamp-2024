package io.reactivestax.producer.util.database;

public interface ConnectionUtil<T> {
    T getConnection();
}
