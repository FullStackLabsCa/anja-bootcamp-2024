package io.reactivestax.factory;

import io.reactivestax.Cache;
import io.reactivestax.enums.EvictionPolicy;
import io.reactivestax.model.Value;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class CacheFactoryTest {
    CacheFactory<String, Value> cacheFactory = new CacheFactory<>();

    @Test
    void testCreateCacheStorage() {
        Cache<String, Value> cache = cacheFactory.createCacheStorage(EvictionPolicy.FIFO);
        assertNotNull(cache);
    }
}
