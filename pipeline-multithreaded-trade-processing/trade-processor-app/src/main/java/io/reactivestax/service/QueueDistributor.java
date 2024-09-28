package io.reactivestax.service;

import java.util.concurrent.*;

public class QueueDistributor {
    static final ConcurrentMap<String, Integer> concurrentQueueDistributorMap = new ConcurrentHashMap<>();
    static int queueNumber = 1;

    static LinkedBlockingQueue<String> transactionQueueOne = new LinkedBlockingQueue<>();
    static LinkedBlockingQueue<String> transactionQueueTwo = new LinkedBlockingQueue<>();
    static LinkedBlockingQueue<String> transactionQueueThree = new LinkedBlockingQueue<>();

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
                transactionQueueOne.put(tradeId);
                break;
            case 2:
                transactionQueueTwo.put(tradeId);
                break;
            case 3:
                transactionQueueThree.put(tradeId);
                break;
            default:
        }
    }
}
