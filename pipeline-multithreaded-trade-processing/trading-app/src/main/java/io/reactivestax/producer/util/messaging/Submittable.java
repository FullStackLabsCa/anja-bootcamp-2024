package io.reactivestax.producer.util.messaging;

public interface Submittable<T> {
    void submitTask(T processor);
}
