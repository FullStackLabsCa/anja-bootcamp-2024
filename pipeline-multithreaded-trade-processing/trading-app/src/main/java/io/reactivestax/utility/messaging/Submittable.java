package io.reactivestax.utility.messaging;

public interface Submittable<T> {
    void submitTask(T processor);
}
