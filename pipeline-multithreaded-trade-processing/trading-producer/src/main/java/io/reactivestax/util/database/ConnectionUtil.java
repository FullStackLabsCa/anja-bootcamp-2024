package io.reactivestax.util.database;

public interface ConnectionUtil<T> {
    T getConnection();
}
