package io.reactivestax.util.messaging;

public interface Submittable<T> {
    void submitTask(T processor);
}
