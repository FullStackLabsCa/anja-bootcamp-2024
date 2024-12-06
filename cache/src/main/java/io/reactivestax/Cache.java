package io.reactivestax;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Cache<K, V> implements CacheStorage<K, V> {
    private static final Logger logger = Logger.getLogger(Cache.class.getName());
    private final Map<K, ValueTtl<V>> cacheMap;

    public Cache() {
        cacheMap = new ConcurrentHashMap<>();
        cleanUp();
    }

    private void cleanUp() {
        Timer timer = new Timer(true);
        logger.info("Demon thread started.");
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                logger.info("Demon thread running.");
                cacheMap.forEach((key, valueTtl) -> {
                    Duration duration = Duration.ofMillis(valueTtl.getTtl());
                    LocalDateTime lastAccessTime = valueTtl.getLastAccessTime();
                    LocalDateTime lastAccessTimePlusTtl = lastAccessTime.plus(duration);
                    LocalDateTime currentTime = LocalDateTime.now();
                    int compared = lastAccessTimePlusTtl.compareTo(currentTime);
                    if (compared < 0) {
                        cacheMap.remove(key);
                        logger.log(Level.INFO, "Daemon thread removed object with key: {0}", new Object[]{key});
                    }
                });
            }
        }, 1000, 10000);
    }

    @Override
    public void put(K key, V value) {
        ValueTtl<V> valueTtl = new ValueTtl.ValueTtlBuilder<V>().value(value).build();
        cacheMap.put(key, valueTtl);
        logger.log(Level.INFO, "Added object with key: {0}", new Object[]{key});
    }

    @Override
    public void put(K key, V value, int ttl) {
        ValueTtl<V> valueTtl = new ValueTtl.ValueTtlBuilder<V>().value(value).ttl(ttl).build();
        cacheMap.put(key, valueTtl);
        logger.log(Level.INFO, "Added object with key: {0}", new Object[]{key});
    }

    @Override
    public V get(K key) {
        Optional<ValueTtl<V>> valueTtlOptional = Optional.ofNullable(cacheMap.get(key));
        return valueTtlOptional.map(valueTtl -> {
            valueTtl.setLastAccessTime(LocalDateTime.now());
            cacheMap.put(key, valueTtl);
            return valueTtl.getValue();
        }).orElse(null);
    }

    @Override
    public boolean remove(K key) {
        Optional<ValueTtl<V>> optionalValueTtl = Optional.ofNullable(cacheMap.get(key));
        return optionalValueTtl.map(valueTtl -> {
            cacheMap.remove(key);
            logger.log(Level.INFO, "Removed object with key: {0}", new Object[]{key});
            return true;
        }).orElse(false);
    }

    @Override
    public int size() {
        return cacheMap.size();
    }

    @Override
    public void clear() {
        cacheMap.clear();
    }

    @Override
    public Set<K> keys() {
        return cacheMap.keySet();
    }
}
