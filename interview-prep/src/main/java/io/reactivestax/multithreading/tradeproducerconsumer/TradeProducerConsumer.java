package io.reactivestax.multithreading.tradeproducerconsumer;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.IntStream;

public class TradeProducerConsumer {

    public static void main(String[] args) {
        BlockingQueue<Trade> blockingQueue = new ArrayBlockingQueue<>(10);
        ExecutorService producerPool = Executors.newFixedThreadPool(5);
        ExecutorService consumerPool = Executors.newFixedThreadPool(5);
        IntStream.range(0, 5).forEach(i -> producerPool.submit(new Producer(blockingQueue)));
        IntStream.range(0, 5).forEach(i -> consumerPool.submit(new Consumer(blockingQueue)));
    }
}
