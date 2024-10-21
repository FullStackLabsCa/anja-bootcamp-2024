package io.reactivestax.utility.messaging;

public interface TransactionRetryer {
    void retryTransaction(String tradeId, String queueName);
}
