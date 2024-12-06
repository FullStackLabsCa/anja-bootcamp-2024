package io.reactivestax;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Cache<K, V> implements CacheStorage<K, V> {
    private final Logger logger = Logger.getLogger(Cache.class.getName());
    private final Map<K, ValueTtl<K, V>> cacheMap = new ConcurrentHashMap<>();

    @Override
    public void put(K key, V value) {
        ValueTtl<K, V> valueTtl = new ValueTtl.ValueTtlBuilder<K, V>().key(key).value(value).build();
        cacheMap.put(key, valueTtl);
        logger.log(Level.INFO, "Added object with key: {0}", new Object[]{key});
    }

    @Override
    public void put(K key, V value, int ttl) {
        ValueTtl<K, V> valueTtl = new ValueTtl.ValueTtlBuilder<K, V>().key(key).value(value).ttl(ttl).build();
        cacheMap.put(key, valueTtl);
        logger.log(Level.INFO, "Added object with key: {0}", new Object[]{key});
    }

    @Override
    public V get(K key) {
        Optional<ValueTtl<K, V>> valueTtlOptional = Optional.ofNullable(cacheMap.get(key));
        return valueTtlOptional.map(valueTtl -> {
            valueTtl.setLastAccessTime(LocalDateTime.now());
            cacheMap.put(key, valueTtl);
            return valueTtl.getValue();
        }).orElse(null);
    }

    @Override
    public boolean remove(K key) {
        Optional<ValueTtl<K, V>> optionalValueTtl = Optional.ofNullable(cacheMap.get(key));
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
    public List<K> keys() {
        return cacheMap.keySet().stream().toList();
    }

    @Override
    public List<ValueTtl<K, V>> values() {
        return cacheMap.values().stream().toList();
    }
}
