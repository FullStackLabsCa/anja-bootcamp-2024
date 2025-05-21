package io.reactivestax.multithreading.tradeproducerconsumer;

import java.util.concurrent.BlockingQueue;

public class Consumer implements Runnable {

    private int i = 0;
    private final BlockingQueue<Trade> tradeBlockingQueue;

    public Consumer(BlockingQueue<Trade> tradeBlockingQueue) {
        this.tradeBlockingQueue = tradeBlockingQueue;
    }

    @Override
    public void run() {
        while (i != 20) {
            try {
                Thread.sleep(200);
                Trade trade = tradeBlockingQueue.take();
                i++;
                if(System.currentTimeMillis() - trade.getTimestamp() <= 5000) System.out.println(trade);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }
}
