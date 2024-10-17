package io.reactivestax.service;

public interface Submittable<T> {
    void submitTask(T processor);
}
