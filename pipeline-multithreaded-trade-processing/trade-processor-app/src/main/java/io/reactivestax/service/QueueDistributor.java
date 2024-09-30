package io.reactivestax.service;

import io.reactivestax.utility.MaintainStaticValues;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.*;

public class QueueDistributor {
    static Random random = new Random();

    static List<LinkedBlockingDeque<String>> transactionDeque = new ArrayList<>();

    static BlockingDeque<String> deadLetterTransactionDeque = new LinkedBlockingDeque<>();

    private QueueDistributor() {
    }

    public static LinkedBlockingDeque<String> getTransactionDeque(int index) {
        return transactionDeque.get(index);
    }

    public static void initializeQueue() {
        int count = MaintainStaticValues.getTradeProcessorQueueCount();
        while (count-- != 0) {
            transactionDeque.add(new LinkedBlockingDeque<>());
        }
    }

    public static int getQueueNumber() {
        return random.nextInt(MaintainStaticValues.getTradeProcessorQueueCount());
    }

    public static void giveToQueue(String tradeId, int queueNumber) throws InterruptedException {
        LinkedBlockingDeque<String> linkedBlockingDeque = transactionDeque.get(queueNumber);
        linkedBlockingDeque.put(tradeId);
        transactionDeque.set(queueNumber, linkedBlockingDeque);
    }
}
