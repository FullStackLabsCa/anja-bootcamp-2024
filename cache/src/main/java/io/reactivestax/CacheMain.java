package io.reactivestax;

import java.util.concurrent.CountDownLatch;

public class CacheMain {
    public static void main(String[] args) {
        CountDownLatch countDownLatch = new CountDownLatch(1);
        Cache<String, Value> cache = new Cache<>();

        Value value = new Value("Jain", "2266985174");

        cache.put("Anant", value);
        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
