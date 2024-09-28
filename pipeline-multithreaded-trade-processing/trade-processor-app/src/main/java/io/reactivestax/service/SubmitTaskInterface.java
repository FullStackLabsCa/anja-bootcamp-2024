package io.reactivestax.service;

public interface SubmitTaskInterface<T> {
    void submitTask(T processor);
}
