package io.reactivestax.service;

public interface RetryTransaction {
    void retryTransaction(String tradeId) throws InterruptedException;
}
