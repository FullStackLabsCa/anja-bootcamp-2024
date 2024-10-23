package io.reactivestax.util.messaging;

import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class QueueDistributor {
    static ConcurrentMap<String, Integer> concurrentQueueDistributorMap = new ConcurrentHashMap<>();

    static int queueNumber = 0;

    static Random random = new Random();

    private QueueDistributor() {
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
}
