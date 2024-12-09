package io.reactivestax.factory;

import io.reactivestax.Cache;
import io.reactivestax.enums.EvictionPolicy;

import java.util.*;

public class CacheFactory<K, V> {
    private final Map<EvictionPolicy, List<Cache<K, V>>> map = new EnumMap<>(EvictionPolicy.class);

    public Cache<K, V> createCacheStorage(EvictionPolicy policy) {
        Cache<K, V> cache = new Cache<>();
        Optional<List<Cache<K, V>>> optionalCaches = Optional.ofNullable(map.get(policy));
        optionalCaches.ifPresentOrElse(caches -> caches.add(cache),
                () -> {
                    List<Cache<K, V>> list = new ArrayList<>();
                    list.add(cache);
                    map.put(policy, list);
                    summonDaemonThread(policy, list);
                });

        return cache;
    }

    private void summonDaemonThread(EvictionPolicy policy, List<Cache<K, V>> list) {
        DaemonThreadFactory<K, V> daemonThreadFactory = new DaemonThreadFactory<>();
        daemonThreadFactory.summonDaemon(policy, list);
    }
}
