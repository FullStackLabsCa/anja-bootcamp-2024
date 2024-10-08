package io.reactivestax.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.*;

public class QueueDistributor {
    static ConcurrentMap<String, Integer> concurrentQueueDistributorMap = new ConcurrentHashMap<>();

    static int queueNumber = 0;

    static LinkedBlockingQueue<String> chunkQueue = new LinkedBlockingQueue<>();

    static List<LinkedBlockingDeque<String>> transactionDeque = new ArrayList<>();

    static BlockingQueue<String> deadLetterQueue = new LinkedBlockingQueue<>();

    static ConcurrentMap<String, Integer> retryMap = new ConcurrentHashMap<>();

    static Random random = new Random();

    private QueueDistributor() {
    }

    public static LinkedBlockingDeque<String> getTransactionDeque(int index) {
        return transactionDeque.get(index);
    }

    public static synchronized int figureOutTheNextQueue(String value, boolean useMap, String distAlgorithm, int queueCount) {
        int queue;
        if (useMap) {
            if (concurrentQueueDistributorMap.containsKey(value)) {
                queue = concurrentQueueDistributorMap.get(value);
            } else {
                queue = getQueueNumberNumberUsingAlgorithm(distAlgorithm, queueCount);
                concurrentQueueDistributorMap.put(value, queue);
            }
        } else queue = getQueueNumberNumberUsingAlgorithm(distAlgorithm, queueCount);

        return queue;
    }

    public static int getQueueNumberNumberUsingAlgorithm(String distAlgorithm, int queueCount) {
        int queue = 0;
        if (distAlgorithm.equals("random")) {
            queue = random.nextInt(queueCount);
        } else {
            queue = queueNumber;
            queueNumber++;
            if (queueNumber >= queueCount) {
                queueNumber = 0;
            }
        }
        return queue;
    }

    public static void initializeQueue(int queueCount) {
        int count = queueCount;
        while (count-- != 0) {
            transactionDeque.add(new LinkedBlockingDeque<>());
        }
    }

    public static void giveToTradeQueue(String tradeId, int queueNumber) throws InterruptedException {
        LinkedBlockingDeque<String> linkedBlockingDeque = transactionDeque.get(queueNumber);
        linkedBlockingDeque.put(tradeId);
        transactionDeque.set(queueNumber, linkedBlockingDeque);
    }

    public static synchronized Integer getValueFromRetryMap(String tradeId) {
        return retryMap.getOrDefault(tradeId, 0);
    }

    public static synchronized void setValueInRetryMap(String tradeId, Integer retryCount) {
        QueueDistributor.retryMap.put(tradeId, retryCount);
    }

    public static synchronized void removeValueFromRetryMap(String tradeId){
        QueueDistributor.retryMap.remove(tradeId);
    }

    public static synchronized ConcurrentMap<String, Integer> getRetryMap() {
        return retryMap;
    }

    public static synchronized void setDeadLetterQueue(String tradeId) throws InterruptedException {
        QueueDistributor.deadLetterQueue.put(tradeId);
    }

    public static synchronized BlockingQueue<String> getDeadLetterQueue() {
        return deadLetterQueue;
    }
}
