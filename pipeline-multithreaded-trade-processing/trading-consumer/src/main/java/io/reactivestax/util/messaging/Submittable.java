package io.reactivestax.consumer.util.messaging;

public interface Submittable<T> {
    void submitTask(T processor);
}
