package io.reactivestax.producer.service;

import java.io.IOException;
import java.util.logging.Logger;

public class ChunkFileGenerator implements Runnable {
    Logger logger = Logger.getLogger(ChunkFileGenerator.class.getName());

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
