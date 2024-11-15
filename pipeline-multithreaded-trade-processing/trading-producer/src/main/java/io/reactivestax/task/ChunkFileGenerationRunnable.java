package io.reactivestax.task;

import io.reactivestax.service.ChunkGeneratorService;

import java.io.IOException;
import java.util.logging.Logger;

public class ChunkFileGenerationRunnable implements Runnable {
    Logger logger = Logger.getLogger(ChunkFileGenerationRunnable.class.getName());

    @Override
    public void run() {
        try {
            ChunkGeneratorService.getInstance().generateChunks();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } catch (IOException e) {
            logger.warning("IO Exception.");
        }
    }
}
