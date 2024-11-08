package io.reactivestax.util.messaging;

public interface TransactionRetryer {
    void retryTradeProcessing(String tradeId, String queueName);
}
