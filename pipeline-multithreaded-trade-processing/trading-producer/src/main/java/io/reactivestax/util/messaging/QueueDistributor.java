package io.reactivestax.util.messaging;

import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class QueueDistributor {
    private static QueueDistributor instance;
    private final ConcurrentMap<String, Integer> concurrentQueueDistributorMap = new ConcurrentHashMap<>();

    private int queueNumber = 0;

    private final Random random = new Random();

    private QueueDistributor() {
    }

    public static synchronized QueueDistributor getInstance() {
        if (instance == null) {
            instance = new QueueDistributor();
        }

        return instance;
    }

    public synchronized int figureOutTheNextQueue(String value, boolean useMap, String distAlgorithm, int queueCount) {
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

    public int getQueueNumberNumberUsingAlgorithm(String distAlgorithm, int queueCount) {
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
