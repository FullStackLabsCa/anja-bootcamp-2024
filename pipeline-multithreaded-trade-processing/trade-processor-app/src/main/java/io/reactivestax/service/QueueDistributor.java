package io.reactivestax.service;

import java.util.concurrent.*;

public class QueueDistributor {
    static final ConcurrentMap<String, Integer> concurrentQueueDistributorMap = new ConcurrentHashMap<>();
    static int queueNumber = 1;

    static LinkedBlockingDeque<String> transactionDequeOne = new LinkedBlockingDeque<>();
    static LinkedBlockingDeque<String> transactionDequeTwo = new LinkedBlockingDeque<>();
    static LinkedBlockingDeque<String> transactionDequeThree = new LinkedBlockingDeque<>();
    static BlockingDeque<String> deadLetterTransactionDeque = new LinkedBlockingDeque<>();

    private QueueDistributor() {
    }

    public static int getQueueNumber(String account) {
        int queue = 0;
        if (concurrentQueueDistributorMap.containsKey(account)) {
            queue = concurrentQueueDistributorMap.get(account);
        } else {
            concurrentQueueDistributorMap.put(account, queueNumber);
            queue = queueNumber;
            queueNumber++;
            if (queueNumber > 3) {
                queueNumber = 1;
            }
        }

        return queue;
    }

    public static void giveToQueue(String tradeId, int queueNumber) throws InterruptedException {
        switch (queueNumber) {
            case 1:
                transactionDequeOne.put(tradeId);
                break;
            case 2:
                transactionDequeTwo.put(tradeId);
                break;
            case 3:
                transactionDequeThree.put(tradeId);
                break;
            default:
        }
    }
}
