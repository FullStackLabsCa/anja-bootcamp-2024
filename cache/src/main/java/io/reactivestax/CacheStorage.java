package io.reactivestax;

import java.util.Set;

public interface CacheStorage<K, V> {
    void put(K key, V value);

    void put(K key, V value, int ttl);

    V get(K key);

    boolean remove(K key);

    int size();

    void clear();

    Set<K> keys();
}
