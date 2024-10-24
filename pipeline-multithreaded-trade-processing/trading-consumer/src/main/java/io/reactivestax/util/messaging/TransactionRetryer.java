package io.reactivestax.util.messaging;

public interface TransactionRetryer {
    void retryTransaction(String tradeId, String queueName);
}
