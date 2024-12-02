package io.reactivestax.task;

import io.reactivestax.service.ChunkProcessorService;
import io.reactivestax.util.QueueProvider;

import java.util.logging.Logger;

public class ChunkFileProcessor implements Runnable {
    private final Logger logger = Logger.getLogger(ChunkFileProcessor.class.getName());

    @Override
    public void run() {
        try {
            while (true) {
                String filePath = QueueProvider.getInstance().getChunkQueue().take();
                if (!filePath.isEmpty()) {
                    ChunkProcessorService.getInstance().processChunk(filePath);
                    break;
                }
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            logger.warning("Interrupted Exception occurred.");
        }
    }
}
