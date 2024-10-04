package io.reactivestax;

import io.reactivestax.service.ChunkGeneratorAndProcessorService;
import io.reactivestax.utility.ApplicationPropertiesUtils;

import java.util.logging.Logger;

public class TradeProcessorRunner {
    static Logger logger = Logger.getLogger(TradeProcessorRunner.class.getName());

    public void start() {
        logger.info("Started project.");
        logger.info("Reading application properties and loading static values.");
        ApplicationPropertiesUtils applicationPropertiesUtils =
                new ApplicationPropertiesUtils("application.properties");
        ChunkGeneratorAndProcessorService chunkGeneratorAndProcessorService = new ChunkGeneratorAndProcessorService();
        chunkGeneratorAndProcessorService.setupDataSourceAndStartGeneratorsAndProcessors(applicationPropertiesUtils);

    }
    public static void main(String[] args) {
        TradeProcessorRunner runner = new TradeProcessorRunner();
        runner.start();
    }
}
