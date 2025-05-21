package io.reactivestax.multithreading.tradeproducerconsumer;

import java.util.concurrent.BlockingQueue;

public class Producer implements Runnable {

    private int i = 0;
    private final BlockingQueue<Trade> tradeBlockingQueue;

    public Producer(BlockingQueue<Trade> tradeBlockingQueue) {
        this.tradeBlockingQueue = tradeBlockingQueue;
    }

    @Override
    public void run() {
        while (i != 20) {

            try {
                long time = System.currentTimeMillis();
                Thread.sleep(5000);
                tradeBlockingQueue.put(new Trade((long) i, "AAPL", i + 10, i + 100.0, System.currentTimeMillis()));
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }

            i++;
        }
    }
}
