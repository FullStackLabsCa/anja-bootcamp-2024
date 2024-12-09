package io.reactivestax.factory;

import io.reactivestax.Cache;
import io.reactivestax.enums.EvictionPolicy;
import io.reactivestax.model.Value;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DaemonThreadFactoryTest {
    private final DaemonThreadFactory<String, Value> daemonThreadFactory = new DaemonThreadFactory<>();

    private final List<Cache<String, Value>> cacheList = new ArrayList<>();

    @BeforeEach
    void setUp() {
        Cache<String, Value> cache = new Cache<>();
        IntStream.range(1, 21).forEach(i -> cache.put("Key" + i, new Value("Anant", "2266985174")));
        cacheList.add(cache);
    }

    @Test
    void testSummonDaemonForTtl() {
        daemonThreadFactory.summonDaemon(EvictionPolicy.TTL, cacheList);
        while (!cacheList.get(0).keys().isEmpty()) {
            assertTrue(cacheList.get(0).size() > 0);
        }
        System.out.println(cacheList.get(0).size());
        assertEquals(0, cacheList.get(0).size());
    }

    @Test
    void testSummonDaemonForLfu() {
        daemonThreadFactory.summonDaemon(EvictionPolicy.LFU, cacheList);
        while (cacheList.get(0).size() >=20) {
            assertTrue(cacheList.get(0).size() > 0);
        }
        assertEquals(19, cacheList.get(0).size());
    }

    @Test
    void testSummonDaemonForRr() {
        daemonThreadFactory.summonDaemon(EvictionPolicy.RR, cacheList);
        while (cacheList.get(0).size() >=20) {
            assertTrue(cacheList.get(0).size() > 0);
        }
        assertEquals(19, cacheList.get(0).size());
    }

    @Test
    void testSummonDaemonForLru() {
        daemonThreadFactory.summonDaemon(EvictionPolicy.LRU, cacheList);
        while (cacheList.get(0).size() >=20) {
            assertTrue(cacheList.get(0).size() > 0);
        }
        assertEquals(19, cacheList.get(0).size());
    }

    @Test
    void testSummonDaemonForFifo() {
        daemonThreadFactory.summonDaemon(EvictionPolicy.FIFO, cacheList);
        while (cacheList.get(0).size() >=20) {
            assertTrue(cacheList.get(0).size() > 0);
        }
        assertEquals(19, cacheList.get(0).size());
    }
}
