package io.reactivestax.util;

import lombok.Getter;

import java.util.concurrent.LinkedBlockingQueue;

public class QueueProvider {

    private QueueProvider() {
    }

    @Getter
    protected static final LinkedBlockingQueue<String> chunkQueue = new LinkedBlockingQueue<>(ApplicationPropertiesUtils.getInstance().getNumberOfChunks());
}
