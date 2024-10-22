package io.reactivestax.consumer.util.messaging.inmemory;

import io.reactivestax.consumer.util.ApplicationPropertiesUtils;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.LinkedBlockingQueue;

public class InMemoryQueueProvider {
    private static InMemoryQueueProvider instance;
    private final ApplicationPropertiesUtils applicationPropertiesUtils = ApplicationPropertiesUtils.getInstance();
    @Getter
    private final List<LinkedBlockingDeque<String>> tradeQueues =
            new ArrayList<>(applicationPropertiesUtils.getTradeProcessorQueueCount());

    @Getter
    private final LinkedBlockingQueue<String> chunkQueue = new LinkedBlockingQueue<>();

    @Getter
    private final LinkedBlockingQueue<String> deadLetterQueue = new LinkedBlockingQueue<>();

    private InMemoryQueueProvider() {
        initializeQueue();
    }

    public static synchronized InMemoryQueueProvider getInstance() {
        if (instance == null) {
            instance = new InMemoryQueueProvider();
        }

        return instance;
    }

    private void initializeQueue() {
        for (int i = 0; i < applicationPropertiesUtils.getTradeProcessorQueueCount(); i++) {
            tradeQueues.add(new LinkedBlockingDeque<>());
        }
    }
}
