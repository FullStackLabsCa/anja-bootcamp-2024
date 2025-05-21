package io.reactivestax.problems.producerconsumer;

public class Main {
    public static void main(String[] args) {
        SharedBuffer sharedBuffer = new SharedBuffer(10);
        Object lock = new Object();

        Thread prod1 = new Thread(new Producer(sharedBuffer, lock));
        Thread prod2 = new Thread(new Producer(sharedBuffer, lock));
        Thread cons1 = new Thread(new Consumer(sharedBuffer, lock));
        Thread cons2 = new Thread(new Consumer(sharedBuffer, lock));

        prod1.start();
        prod2.start();
        cons1.start();
        cons2.start();
    }
}
