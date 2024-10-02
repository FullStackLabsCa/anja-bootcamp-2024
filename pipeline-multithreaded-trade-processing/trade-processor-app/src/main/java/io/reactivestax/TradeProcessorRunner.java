package io.reactivestax;

import io.reactivestax.service.ChunkGeneratorAndProcessorService;

import java.util.logging.Logger;

public class TradeProcessorRunner {
    static Logger logger = Logger.getLogger(TradeProcessorRunner.class.getName());
    public static void main(String[] args) {
        logger.info("Started project.");
        ChunkGeneratorAndProcessorService chunkGeneratorAndProcessorService = new ChunkGeneratorAndProcessorService();
        logger.info("Reading application properties and loading static values.");
        chunkGeneratorAndProcessorService.setStaticValues();
        chunkGeneratorAndProcessorService.setupDataSourceAndStartGeneratorsAndProcessors();
    }
}
