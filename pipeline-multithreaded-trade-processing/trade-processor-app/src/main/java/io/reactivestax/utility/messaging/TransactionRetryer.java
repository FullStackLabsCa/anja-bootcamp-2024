package io.reactivestax.utility.messaging;

import java.io.IOException;

public interface TransactionRetryer {
    void retryTransaction(String tradeId) throws InterruptedException, IOException;
}
