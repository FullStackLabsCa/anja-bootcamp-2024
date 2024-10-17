package io.reactivestax.service;

import java.io.IOException;

public interface TransactionRetrier {
    void retryTransaction(String tradeId) throws InterruptedException, IOException;
}
