package io.reactivestax;

import io.reactivestax.service.ChunkGeneratorAndProcessorService;
import io.reactivestax.utility.ApplicationPropertiesUtils;

import java.util.logging.Logger;

public class TradeProcessorRunner {
    static Logger logger = Logger.getLogger(TradeProcessorRunner.class.getName());
    public static void main(String[] args) {
        logger.info("Started project.");
        logger.info("Reading application properties and loading static values.");
        ApplicationPropertiesUtils.loadApplicationProperties();
        ChunkGeneratorAndProcessorService chunkGeneratorAndProcessorService = new ChunkGeneratorAndProcessorService();
        chunkGeneratorAndProcessorService.setupDataSourceAndStartGeneratorsAndProcessors();
    }
}
