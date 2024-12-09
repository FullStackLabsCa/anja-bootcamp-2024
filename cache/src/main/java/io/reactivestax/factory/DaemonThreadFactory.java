package io.reactivestax.factory;

import io.reactivestax.Cache;
import io.reactivestax.enums.EvictionPolicy;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DaemonThreadFactory<K, V> {
    private final Logger logger = Logger.getLogger(DaemonThreadFactory.class.getName());
    private static final String REMOVED_KEY_LOG_MESSAGE = "Daemon task removed object with key: {0}";

    public void summonDaemon(EvictionPolicy policy, List<Cache<K, V>> list) {
        Timer timer = new Timer(true);
        logger.info("Demon thread started.");
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                logger.info("Demon thread running.");
                switch (policy) {
                    case TTL -> ttlDaemonTask(list);
                    case LFU -> lfuDaemonTask(list);
                    case RR -> rrDaemonTask(list);
                    case LRU -> lruDaemonTask(list);
                    case FIFO -> fifoDaemonTask(list);
                }
            }
        }, 1000, 1000);
    }

    private void ttlDaemonTask(List<Cache<K, V>> list) {
        list.forEach(cache -> cache.values().forEach(valueTtl -> {
            Duration duration = Duration.ofMillis(valueTtl.getTtl());
            LocalDateTime lastAccessTime = valueTtl.getLastAccessTime();
            LocalDateTime lastAccessTimePlusTtl = lastAccessTime.plus(duration);
            LocalDateTime currentTime = LocalDateTime.now();
            int compared = lastAccessTimePlusTtl.compareTo(currentTime);
            if (compared < 0) {
                cache.remove(valueTtl.getKey());
                logger.log(Level.INFO, REMOVED_KEY_LOG_MESSAGE, new Object[]{valueTtl.getKey()});
            }
        }));
    }

    private void lfuDaemonTask(List<Cache<K, V>> list) {
        list.forEach(cache -> {
            int size = cache.size();
            if (size >= 20) {
                SortedMap<Integer, K> localDateTimeSortedMap = new TreeMap<>();
                cache.values().forEach(cacheValue -> localDateTimeSortedMap.put(cacheValue.getUsedCount(),
                        cacheValue.getKey()));
                Optional<K> first = localDateTimeSortedMap.values().stream().findFirst();
                first.ifPresent(key -> {
                    cache.remove(key);
                    logger.log(Level.INFO, REMOVED_KEY_LOG_MESSAGE, new Object[]{key});
                });
            }
        });
    }

    private void rrDaemonTask(List<Cache<K, V>> list) {
        list.forEach(cache -> {
            int size = cache.size();
            if (size >= 20) {
                Optional<K> randomCache = cache.keys().stream().findAny();
                randomCache.ifPresent(cacheKey -> {
                    cache.remove(cacheKey);
                    logger.log(Level.INFO, REMOVED_KEY_LOG_MESSAGE, new Object[]{cacheKey});
                });
            }
        });
    }

    private void lruDaemonTask(List<Cache<K, V>> list) {
        list.forEach(cache -> {
            int size = cache.size();
            if (size >= 20) {
                SortedMap<LocalDateTime, K> localDateTimeSortedMap = new TreeMap<>();
                cache.values().forEach(cacheValue -> localDateTimeSortedMap.put(cacheValue.getLastAccessTime(),
                        cacheValue.getKey()));
                Optional<K> first = localDateTimeSortedMap.values().stream().findFirst();
                first.ifPresent(key -> {
                    cache.remove(key);
                    logger.log(Level.INFO, REMOVED_KEY_LOG_MESSAGE, new Object[]{key});
                });
            }
        });
    }

    private void fifoDaemonTask(List<Cache<K, V>> list) {
        list.forEach(cache -> {
            int size = cache.size();
            if (size >= 20) {
                K firstKey = cache.getFirstKey();
                cache.remove(firstKey);
                logger.log(Level.INFO, REMOVED_KEY_LOG_MESSAGE, new Object[]{firstKey});
            }
        });
    }
}
