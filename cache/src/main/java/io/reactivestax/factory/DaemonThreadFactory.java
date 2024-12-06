package io.reactivestax.factory;

import io.reactivestax.Cache;
import io.reactivestax.EvictionPolicy;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DaemonThreadFactory<K, V> {
    private final Logger logger = Logger.getLogger(DaemonThreadFactory.class.getName());

    public void summonDaemon(EvictionPolicy policy, List<Cache<K, V>> list) {
        switch (policy) {
            case TTL -> ttlDaemonThread(list);
            case LFU -> lfuDaemonThread(list);
            case RR -> rrDaemonThread(list);
            case LRU -> lruDaemonThread(list);
            case FIFO -> fifoDaemonThread(list);
            case SIZE_BASED -> sizeBasedDaemonThread(list);
        }
    }

    private void ttlDaemonThread(List<Cache<K, V>> list) {
        Timer timer = new Timer(true);
        logger.info("Demon thread started.");
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                logger.info("Demon thread running.");
                list.forEach(cache -> cache.values().forEach(valueTtl -> {
                    Duration duration = Duration.ofMillis(valueTtl.getTtl());
                    LocalDateTime lastAccessTime = valueTtl.getLastAccessTime();
                    LocalDateTime lastAccessTimePlusTtl = lastAccessTime.plus(duration);
                    LocalDateTime currentTime = LocalDateTime.now();
                    int compared = lastAccessTimePlusTtl.compareTo(currentTime);
                    if (compared < 0) {
                        cache.remove(valueTtl.getKey());
                        logger.log(Level.INFO, "Daemon thread removed object with key: {0}",
                                new Object[]{valueTtl.getKey()});
                    }
                }));
            }
        }, 1000, 1000);
    }

    private void lfuDaemonThread(List<Cache<K, V>> list) {
    }

    private void rrDaemonThread(List<Cache<K, V>> list) {
    }

    private void lruDaemonThread(List<Cache<K, V>> list) {
    }

    private void fifoDaemonThread(List<Cache<K, V>> list) {
    }

    private void sizeBasedDaemonThread(List<Cache<K, V>> list) {
    }
}
