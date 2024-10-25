package io.reactivestax.util;

import lombok.Getter;
import lombok.Setter;

import java.util.concurrent.LinkedBlockingQueue;

public class QueueProvider {
    private static QueueProvider instance;
    @Getter
    @Setter
    private LinkedBlockingQueue<String> chunkQueue;

    private QueueProvider() {
        chunkQueue = new LinkedBlockingQueue<>(ApplicationPropertiesUtils.getInstance().getNumberOfChunks());
    }

    public static synchronized QueueProvider getInstance() {
        if (instance == null) {
            instance = new QueueProvider();
        }

        return instance;
    }
}
