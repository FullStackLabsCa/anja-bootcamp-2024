package io.reactivestax.utility.messaging.inmemory;

import io.reactivestax.utility.messaging.TransactionRetryer;

public class InMemoryRetry implements TransactionRetryer {
    private static InMemoryRetry instance;

    private InMemoryRetry() {
    }

    public static synchronized InMemoryRetry getInstance() {
        if (instance == null) {
            instance = new InMemoryRetry();
        }

        return instance;
    }

    @Override
    public void retryTransaction(String tradeId, String queueName) {
    }
}
