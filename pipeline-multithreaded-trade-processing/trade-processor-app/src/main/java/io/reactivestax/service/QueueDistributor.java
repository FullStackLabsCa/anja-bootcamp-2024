package io.reactivestax.service;

import io.reactivestax.utility.ApplicationPropertiesUtils;

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
        if (ApplicationPropertiesUtils.isTradeDistributionUseMap()) {
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
        int quue = 0;
        if (ApplicationPropertiesUtils.getTradeDistributionAlgorithm().equals("random")) {
            quue = random.nextInt(ApplicationPropertiesUtils.getTradeProcessorQueueCount());
        } else {
            quue = queueNumber;
            queueNumber++;
            if (queueNumber >= ApplicationPropertiesUtils.getTradeProcessorQueueCount()) {
                queueNumber = 0;
            }
        }
        return quue;
    }

    public static void initializeQueue() {
        int count = ApplicationPropertiesUtils.getTradeProcessorQueueCount();
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
