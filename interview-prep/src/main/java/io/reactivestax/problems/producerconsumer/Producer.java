package io.reactivestax.problems.producerconsumer;

public class Producer implements Runnable {
    private final SharedBuffer sharedBuffer;
    private final Object lock;
    private int count = 50;

    public Producer(SharedBuffer sharedBuffer, Object lock) {
        this.sharedBuffer = sharedBuffer;
        this.lock = lock;
    }


    @Override
    public void run() {
        while (count != 0) {
            try {
                synchronized (lock) {
                    while (sharedBuffer.checkBufferFull()) {
                        System.out.println("Buffer full, waiting to produce.....");
                        lock.wait();
                    }
                    System.out.println("Produced Message...");
                    this.sharedBuffer.addToBuffer("Message" + count);
                    this.lock.notifyAll();
                    count--;
                }
                Thread.sleep(100);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }
}
