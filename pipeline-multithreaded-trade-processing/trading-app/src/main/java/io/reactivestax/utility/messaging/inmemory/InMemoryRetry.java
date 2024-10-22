package io.reactivestax.utility.messaging.inmemory;

import io.reactivestax.utility.ApplicationPropertiesUtils;
import io.reactivestax.utility.messaging.TransactionRetryer;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class InMemoryRetry implements TransactionRetryer {
    private static InMemoryRetry instance;

    private final Map<String, Integer> retryMap = new ConcurrentHashMap<>();

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
        Integer retryCount = retryMap.getOrDefault(tradeId, 0);
        try {
            if (retryCount < ApplicationPropertiesUtils.getInstance().getMaxRetryCount()) {
                retryMap.put(tradeId, retryCount + 1);
                InMemoryQueueProvider.getInstance().getTradeQueues().get(Integer.parseInt(queueName.substring(queueName.length() - 1))).putFirst(tradeId);
            } else {
                retryMap.remove(tradeId);
                InMemoryQueueProvider.getInstance().getDeadLetterQueue().put(tradeId);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
