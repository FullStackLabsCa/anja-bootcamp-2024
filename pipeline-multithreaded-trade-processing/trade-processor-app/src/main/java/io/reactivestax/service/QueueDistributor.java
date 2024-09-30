package io.reactivestax.service;

import java.util.concurrent.*;

public class QueueDistributor {
    static ConcurrentMap<String, Integer> concurrentQueueDistributorMap = new ConcurrentHashMap<>();
    static int queueNumber = 1;

    static LinkedBlockingDeque<String> transactionDequeOne = new LinkedBlockingDeque<>();
    static LinkedBlockingDeque<String> transactionDequeTwo = new LinkedBlockingDeque<>();
    static LinkedBlockingDeque<String> transactionDequeThree = new LinkedBlockingDeque<>();
    static BlockingDeque<String> deadLetterTransactionDeque = new LinkedBlockingDeque<>();

    private QueueDistributor() {
    }

    public static ConcurrentMap<String, Integer> getConcurrentQueueDistributorMap() {
        return concurrentQueueDistributorMap;
    }

    public static void setQueueNumber(int queueNumber) {
        QueueDistributor.queueNumber = queueNumber;
    }

    public static void setConcurrentQueueDistributorMap(ConcurrentMap<String, Integer> concurrentQueueDistributorMap) {
        QueueDistributor.concurrentQueueDistributorMap = concurrentQueueDistributorMap;
    }

    public static LinkedBlockingDeque<String> getTransactionDequeOne() {
        return transactionDequeOne;
    }

    public static void setTransactionDequeOne(LinkedBlockingDeque<String> transactionDequeOne) {
        QueueDistributor.transactionDequeOne = transactionDequeOne;
    }

    public static void setTransactionDequeTwo(LinkedBlockingDeque<String> transactionDequeTwo) {
        QueueDistributor.transactionDequeTwo = transactionDequeTwo;
    }

    public static void setTransactionDequeThree(LinkedBlockingDeque<String> transactionDequeThree) {
        QueueDistributor.transactionDequeThree = transactionDequeThree;
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
