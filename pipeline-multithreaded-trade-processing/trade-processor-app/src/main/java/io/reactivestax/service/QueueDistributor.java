package io.reactivestax.service;

import io.reactivestax.utility.MaintainStaticValues;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.*;

public class QueueDistributor {
    static ConcurrentMap<String, Integer> concurrentQueueDistributorMap = new ConcurrentHashMap<>();

    static int queueNumber = 0;

    static LinkedBlockingQueue<String> chunkQueue = new LinkedBlockingQueue<>();

    static List<LinkedBlockingDeque<String>> transactionDeque = new ArrayList<>();

    static BlockingDeque<String> deadLetterTransactionDeque = new LinkedBlockingDeque<>();

    static Random random = new Random();

    private QueueDistributor() {
    }

    public static LinkedBlockingDeque<String> getTransactionDeque(int index) {
        return transactionDeque.get(index);
    }

    public static synchronized int figureOutTheNextQueue(String value) {
        int queue;
        if (MaintainStaticValues.isTradeDistributionUseMap()) {
            if (concurrentQueueDistributorMap.containsKey(value)) {
                queue = concurrentQueueDistributorMap.get(value);
            } else {
                queue = getQueueNumberNumberUsingAlgorithm();
                concurrentQueueDistributorMap.put(value, queue);
            }
        } else queue = getQueueNumberNumberUsingAlgorithm();

        return queue;
    }

    public static int getQueueNumberNumberUsingAlgorithm() {
        if (MaintainStaticValues.getTradeDistributionAlgorithm().equals("random")) {
            return random.nextInt(MaintainStaticValues.getTradeProcessorQueueCount());
        } else {
            queueNumber++;
            if (queueNumber >= MaintainStaticValues.getTradeProcessorQueueCount()) {
                queueNumber = 0;
            }
        }
        return queueNumber;
    }

    public static void initializeQueue() {
        int count = MaintainStaticValues.getTradeProcessorQueueCount();
        while (count-- != 0) {
            transactionDeque.add(new LinkedBlockingDeque<>());
        }
    }

    public static void giveToTradeQueue(String tradeId, int queueNumber) throws InterruptedException {
        LinkedBlockingDeque<String> linkedBlockingDeque = transactionDeque.get(queueNumber);
        linkedBlockingDeque.put(tradeId);
        transactionDeque.set(queueNumber, linkedBlockingDeque);
    }
}
