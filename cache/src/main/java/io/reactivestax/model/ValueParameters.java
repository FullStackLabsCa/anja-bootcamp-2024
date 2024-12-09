package io.reactivestax.model;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ValueParameters<K, V> {
    private K key;
    private V value;
    private long ttl = 10000;
    private LocalDateTime lastAccessTime = LocalDateTime.now();
    private int usedCount = 0;
}
