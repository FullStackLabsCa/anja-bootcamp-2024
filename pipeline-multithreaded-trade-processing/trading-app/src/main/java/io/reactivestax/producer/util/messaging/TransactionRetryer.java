package io.reactivestax.producer.util.messaging;

public interface TransactionRetryer {
    void retryTransaction(String tradeId, String queueName);
}
