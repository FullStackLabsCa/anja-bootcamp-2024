package io.reactivestax.tradingproducer.task;

import io.reactivestax.tradingproducer.service.ChunkProcessorService;
import io.reactivestax.tradingproducer.util.QueueProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.logging.Logger;

@Component
@Scope("prototype")
public class ChunkFileProcessor implements Runnable {
    private final QueueProvider queueProvider;
    private final ChunkProcessorService chunkProcessorService;
    private final Logger logger = Logger.getLogger(ChunkFileProcessor.class.getName());

    @Autowired
    public ChunkFileProcessor(QueueProvider queueProvider, ChunkProcessorService chunkProcessorService) {
        this.queueProvider = queueProvider;
        this.chunkProcessorService = chunkProcessorService;
    }

    @Override
    public void run() {
        try {
            while (true) {
                String filePath = queueProvider.getChunkQueue().take();
                if (!filePath.isEmpty()) {
                    chunkProcessorService.processChunk(filePath);
                    break;
                }
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            logger.warning("Interrupted Exception occurred.");
        }
    }
}
