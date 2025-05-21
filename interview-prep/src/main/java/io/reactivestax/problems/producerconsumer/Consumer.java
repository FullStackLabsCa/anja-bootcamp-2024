package io.reactivestax.problems.producerconsumer;

public class Consumer implements Runnable {
    private final SharedBuffer sharedBuffer;
    private final Object lock;
    private int count = 50;

    public Consumer(SharedBuffer sharedBuffer, Object lock) {
        this.sharedBuffer = sharedBuffer;
        this.lock = lock;
    }


    @Override
    public void run() {
        while (count != 0) {
            try {
                synchronized (lock) {
                    while (sharedBuffer.checkBufferEmpty()) {
                        System.out.println("Buffer empty, waiting to consume.....");
                        lock.wait();
                    }
                    System.out.println("Message from buffer: " + this.sharedBuffer.getValueFromBuffer());
                    this.lock.notifyAll();
                    count--;
                }
                Thread.sleep(500);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }
}
