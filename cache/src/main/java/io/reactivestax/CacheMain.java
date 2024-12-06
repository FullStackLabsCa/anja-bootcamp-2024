package io.reactivestax;

import io.reactivestax.factory.CacheFactory;

import java.util.concurrent.CountDownLatch;

public class CacheMain {
    public static void main(String[] args) {
        CountDownLatch countDownLatch = new CountDownLatch(1);
        CacheFactory<String, Value> cacheFactory = new CacheFactory<>();
        Cache<String, Value> cache = cacheFactory.createCacheStorage(EvictionPolicy.TTL);

        Value value = new Value("Jain", "2266985174");

        cache.put("Anant", value);
        try {
            Thread.sleep(20000);
            cache.put("Ankit", value);
            countDownLatch.await();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
