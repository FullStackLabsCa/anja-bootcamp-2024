package io.reactivestax;

import io.reactivestax.model.ValueParameters;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Cache<K, V> implements CacheStorage<K, V> {
    private final Logger logger = Logger.getLogger(Cache.class.getName());
    private final Map<K, ValueParameters<K, V>> cacheMap = new ConcurrentHashMap<>();
    private final Queue<K> queueOfKeys = new LinkedList<>();

    public K getFirstKey() {
        return queueOfKeys.poll();
    }

    @Override
    public void put(K key, V value) {
        ValueParameters<K, V> valueParameters = new ValueParameters<>();
        valueParameters.setKey(key);
        valueParameters.setValue(value);
        cacheMap.put(key, valueParameters);
        if (!queueOfKeys.contains(key)) {
            queueOfKeys.add(key);
        }
        logger.log(Level.INFO, "Added object with key: {0}", new Object[]{key});
    }

    @Override
    public void put(K key, V value, int ttl) {
        ValueParameters<K, V> valueParameters = new ValueParameters<>();
        valueParameters.setKey(key);
        valueParameters.setValue(value);
        valueParameters.setTtl(ttl);
        cacheMap.put(key, valueParameters);
        if (!queueOfKeys.contains(key)) {
            queueOfKeys.add(key);
        }
        logger.log(Level.INFO, "Added object with key: {0}", new Object[]{key});
    }

    @Override
    public V get(K key) {
        Optional<ValueParameters<K, V>> valueTtlOptional = Optional.ofNullable(cacheMap.get(key));
        return valueTtlOptional.map(valueParameters -> {
            valueParameters.setLastAccessTime(LocalDateTime.now());
            valueParameters.setUsedCount(valueParameters.getUsedCount() + 1);
            cacheMap.put(key, valueParameters);
            return valueParameters.getValue();
        }).orElse(null);
    }

    @Override
    public boolean remove(K key) {
        Optional<ValueParameters<K, V>> optionalValueTtl = Optional.ofNullable(cacheMap.get(key));
        return optionalValueTtl.map(valueParameters -> {
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
    public List<ValueParameters<K, V>> values() {
        return cacheMap.values().stream().toList();
    }
}
