package io.reactivestax.service;

import io.reactivestax.util.QueueProvider;

import java.sql.SQLException;
import java.util.logging.Logger;

public class ChunkFileProcessor implements Runnable {
    Logger logger = Logger.getLogger(ChunkFileProcessor.class.getName());

    @Override
    public void run() {
        try {
            while (true) {
                String filePath = QueueProvider.getChunkQueue().take();
                if (!filePath.isEmpty()) {
                    ChunkProcessorService.getInstance().processChunk(filePath);
                    break;
                }
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } catch (SQLException e) {
            logger.warning("Something went wrong while establishing database connection.");
        }
    }
}
