package io.reactivestax.consumer.util.messaging;

public interface TransactionRetryer {
    void retryTransaction(String tradeId, String queueName);
}
