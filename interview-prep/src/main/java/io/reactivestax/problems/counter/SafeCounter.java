package io.reactivestax.problems.counter;

public class SafeCounter implements Runnable {
    private final Counter counter;
    private int times = 1000;

    public SafeCounter(Counter counter) {
        this.counter = counter;
    }

    @Override
    public void run() {
        while (times != 0) {
            counter.increment();
            times --;
        }
    }
}

