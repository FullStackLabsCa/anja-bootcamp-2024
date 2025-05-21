package io.reactivestax.problems;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class RBCCapitalMarket {
    static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SS");

    static void formatOutput(String message) {
        System.out.println(String.format("%s:%s", formatter.format(LocalDateTime.now()), message));
    }

    class ThrottleQueue<K, V> {
        public interface Processor<K, V> {
            void process(K k, V v);
        }

        private long throttleIntervalMS;
        private Processor<K, V> processor;

        class DataHolder {
            long deQueueTime;
            V data;

            private DataHolder(long deQueueTime, V data) {
                super();
                this.deQueueTime = deQueueTime;
                this.data = data;
            }
        }

        private LinkedHashMap<K, DataHolder> waitingQueue = new LinkedHashMap<>();

        // Todo add class attributes needed for locking and daemon thread
        private final Object lock = new Object();
        private Thread consumer;

        public ThrottleQueue(TimeUnit intervalUnit, long interval, Processor<K, V> processor) {
            super();
            this.throttleIntervalMS = intervalUnit.convert(interval, TimeUnit.MILLISECONDS);
            this.processor = processor;

            // Todo start the consumer thread for processing

            consumer = new Thread(() -> {
                while (true) {
                    synchronized (lock) {
                        long now = System.currentTimeMillis();
                        Iterator<Map.Entry<K, DataHolder>> iterator = waitingQueue.entrySet().iterator();
                        while (iterator.hasNext()) {
                            Map.Entry<K, DataHolder> next = iterator.next();
                            if (next.getValue().deQueueTime <= now) {
                                this.processor.process(next.getKey(), next.getValue().data);
                                iterator.remove();
                            }
                        }
                    }
                }
            });

            consumer.setDaemon(true);
            consumer.start();
        }

        public void offer(K key, V value) {
            formatOutput("offered k:" + key + ",v:" + value);
            DataHolder data = new DataHolder(System.currentTimeMillis() + throttleIntervalMS, value);
            // Todo thread safe operation to inject key and value
            synchronized (lock) {
                if (waitingQueue.containsKey(key)) {
                    DataHolder dataHolder = waitingQueue.get(key);
                    dataHolder.data = value;
                } else waitingQueue.put(key, data);
                lock.notifyAll();
            }
        }
    }

    public static void main(String[] args) throws InterruptedException {
        RBCCapitalMarket sol = new RBCCapitalMarket();
        RBCCapitalMarket.ThrottleQueue<String, String> tq = sol.new ThrottleQueue<>(TimeUnit.MILLISECONDS, 300L, (k, v) -> {
            formatOutput("Processing k:" + k + ",v:" + v);
        });

        tq.offer("K1", "V1");
        tq.offer("K2", "V2");
        Thread.sleep(100);
        tq.offer("K3", "V3");
        tq.offer("K1", "V4");

        Thread.sleep(10000);
        // expected output is
        // At 300ms, K1/V4, then K2/V2
        // At 400ms, K3/V3
    }
}
