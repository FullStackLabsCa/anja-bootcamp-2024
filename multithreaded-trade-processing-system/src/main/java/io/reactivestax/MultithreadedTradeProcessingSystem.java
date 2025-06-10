package io.reactivestax;

import java.util.*;
import java.util.concurrent.*;
import java.util.stream.IntStream;

public class MultithreadedTradeProcessingSystem {


    public static void main(String[] args) {
        Queue<Trade> tradeQueue = new LinkedBlockingQueue<>();
        Map<String, Double> aggregationMap = new ConcurrentHashMap<>();
        Object lock = new Object();
        Integer schedulerRunCount = 1;
        ExecutorService executorService = Executors.newFixedThreadPool(10);
        ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(1);

        try {
            IntStream.range(0, 5).forEach(index -> executorService.submit(new Producer(tradeQueue)));
            IntStream.range(0, 5).forEach(index -> executorService.submit(new Consumer(tradeQueue, aggregationMap,
                    lock, false, schedulerRunCount)));

            scheduledExecutorService.scheduleAtFixedRate(
                    new Consumer(tradeQueue, aggregationMap, lock, true, schedulerRunCount),
                    3, 3, TimeUnit.SECONDS);

            boolean b = executorService.awaitTermination(15, TimeUnit.SECONDS);
            if (!b) {
                executorService.shutdownNow();
                scheduledExecutorService.shutdownNow();
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    static class Trade {
        String id;
        String symbol;
        int quantity;
        double price;
        long timestamp;

        public String getId() {
            return id;
        }

        public String getSymbol() {
            return symbol;
        }

        public int getQuantity() {
            return quantity;
        }

        public double getPrice() {
            return price;
        }

        public long getTimestamp() {
            return timestamp;
        }

        @Override
        public String toString() {
            return "Trade{" +
                    "id='" + id + '\'' +
                    ", symbol='" + symbol + '\'' +
                    ", quantity=" + quantity +
                    ", price=" + price +
                    ", timestamp=" + timestamp +
                    '}';
        }

        public Trade(String id, String symbol, int quantity, double price, long timestamp) {
            this.id = id;
            this.symbol = symbol;
            this.quantity = quantity;
            this.price = price;
            this.timestamp = timestamp;


        }
    }

    // Producer thread
    static class Producer implements Runnable {
        private final Queue<Trade> tradeQueue;
        private final Random random = new Random();
        List<String> symbols = Arrays.asList(
                "AAPL",
                "MSFT",
                "IBM",
                "AMZN",
                "CSCO",
                "JPM",
                "XOM",
                "JNJ",
                "UPS",
                "CVX",
                "MCD",
                "KO",
                "GOOGL",
                "V",
                "APD",
                "ABT",
                "MRK",
                "APO",
                "UNH",
                "CMCSA"
        );

        public Producer(Queue<Trade> tradeQueue) {
            this.tradeQueue = tradeQueue;
        }

        @Override
        public void run() {
            while (!Thread.currentThread().isInterrupted()) {
                Trade trade = new Trade(
                        String.valueOf(random.nextInt(0, Integer.MAX_VALUE)),
                        symbols.get(random.nextInt(0, symbols.size())),
                        random.nextInt(20, 100),
                        random.nextDouble(100, 200),
                        System.currentTimeMillis());
                tradeQueue.offer(trade);
                System.out.println("Offered trade: " + trade);
                try {
                    Thread.sleep(random.nextLong(300, 800));
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }
    }

    // Consumer thread
    static class Consumer implements Runnable {
        private final Queue<Trade> tradeQueue;
        private final Map<String, Double> aggregationMap;
        private final Object lock;
        private final boolean isScheduledTask;
        private Integer schedulerRunCount;

        public Consumer(Queue<Trade> tradeQueue, Map<String, Double> aggregationMap, Object lock,
                        boolean isScheduledTask, Integer schedulerRunCount) {
            this.tradeQueue = tradeQueue;
            this.aggregationMap = aggregationMap;
            this.lock = lock;
            this.isScheduledTask = isScheduledTask;
            this.schedulerRunCount = schedulerRunCount;
        }

        @Override
        public void run() {
            if (!isScheduledTask) {
                while (!Thread.currentThread().isInterrupted()) {
                    Trade trade = tradeQueue.poll();
                    if (trade != null && System.currentTimeMillis() - trade.getTimestamp() <= 5000) {
                        synchronized (lock) {
                            if (aggregationMap.containsKey(trade.getSymbol())) {
                                aggregationMap.merge(trade.getSymbol(), trade.getPrice() * trade.getQuantity(), Double::sum);
                            } else aggregationMap.put(trade.getSymbol(), trade.getPrice() * trade.getQuantity());
                            lock.notifyAll();
                        }
                    }

                }
            } else {
                System.out.println("At t = " + schedulerRunCount * 3 + "s");
                aggregationMap.forEach((key, value) -> System.out.println(key + " -> " + value));
                schedulerRunCount++;
            }
        }
    }
}
