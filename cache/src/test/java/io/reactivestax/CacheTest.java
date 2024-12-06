package io.reactivestax;

import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class CacheTest {

    private final Value valueSupplier = new Value("Anant", "2266985174");

    @Test
    void testPut() {
        Cache<String, Value> cache = new Cache<>();
        cache.put("UniqueKey1", valueSupplier);
        Value uniqueKeyValue1 = cache.get("UniqueKey1");
        assertEquals(valueSupplier, uniqueKeyValue1);
        cache.put("UniqueKey2", valueSupplier, 30000);
        Value uniqueKeyValue2 = cache.get("UniqueKey2");
        assertEquals(valueSupplier, uniqueKeyValue2);
    }

    @Test
    void testGet() {
        Cache<String, Value> cache = new Cache<>();
        cache.put("uniqueKey", valueSupplier);
        Value uniqueKey = cache.get("uniqueKey");
        assertEquals(valueSupplier, uniqueKey);
    }

    @Test
    void testRemove() {
        Cache<String, Value> cache = new Cache<>();
        cache.put("UniqueKey", valueSupplier);
        boolean deleted = cache.remove("UniqueKey");
        assertTrue(deleted);
        boolean nonExisting = cache.remove("NonExisting");
        assertFalse(nonExisting);
    }

    @Test
    void testSizeWithEmptyMap() {
        Cache<String, Value> cache = new Cache<>();
        assertEquals(0, cache.size());
    }

    @Test
    void testSize() {
        Cache<String, Value> cache = new Cache<>();
        cache.put("UniqueKey1", valueSupplier);
        cache.put("UniqueKey2", valueSupplier);
        assertEquals(2, cache.size());
    }

    @Test
    void testClear() {
        Cache<String, Value> cache = new Cache<>();
        cache.put("UniqueKey1", valueSupplier);
        cache.put("UniqueKey2", valueSupplier);
        assertEquals(2, cache.size());
        cache.clear();
        assertEquals(0, cache.size());
    }

    @Test
    void testKeysWithEmptyMap() {
        Cache<String, Value> cache = new Cache<>();
        assertEquals(0, cache.keys().size());
    }

    @Test
    void testKeys() {
        Cache<String, Value> cache = new Cache<>();
        cache.put("UniqueKey1", valueSupplier);
        cache.put("UniqueKey2", valueSupplier);
        Set<String> keys = cache.keys();
        assertEquals(2, keys.size());
        String key = keys.stream().findAny().orElse(null);
        assertNotNull(key);
    }

    @Test
    void testCleanUp() {
        Cache<String, Value> cache = new Cache<>();
        cache.put("UniqueKey1", valueSupplier);
        while (!cache.keys().isEmpty()) {
            assertTrue(cache.size() > 0);
        }
        assertEquals(0, cache.size());
    }
}
