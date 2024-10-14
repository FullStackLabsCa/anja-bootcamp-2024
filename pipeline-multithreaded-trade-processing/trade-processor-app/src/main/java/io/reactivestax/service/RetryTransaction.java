package io.reactivestax.service;

import java.io.IOException;

public interface RetryTransaction {
    void retryTransaction(String tradeId) throws InterruptedException, IOException;
}
