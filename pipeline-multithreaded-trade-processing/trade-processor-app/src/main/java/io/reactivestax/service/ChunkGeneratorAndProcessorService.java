package io.reactivestax.service;

import io.reactivestax.utility.ApplicationPropertiesUtils;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Logger;
import java.util.stream.Stream;

public class ChunkGeneratorAndProcessorService implements Submittable<ChunkProcessor> {
    private ExecutorService chunkGeneratorExecutorService;
    private ExecutorService chunkProcessorExecutorService;
    Logger logger = Logger.getLogger(ChunkGeneratorAndProcessorService.class.getName());

    public void setupDataSourceAndStartGeneratorsAndProcessors(ApplicationPropertiesUtils applicationProperties) {
        logger.info("Setting up database and project dependencies.");
        try {
            String path = applicationProperties.getFilePath();
            logger.info("Counting total number of lines in the file");
            long numOfLines = fileLineCounter(path);
            applicationProperties.setTotalNoOfLines(numOfLines);
            QueueDistributor.initializeQueue(applicationProperties.getTradeProcessorQueueCount());
            chunkGeneratorExecutorService = Executors.newSingleThreadExecutor();
            chunkProcessorExecutorService =
                    Executors.newFixedThreadPool(applicationProperties.getChunkProcessorThreadCount());
            chunkGeneratorExecutorService.submit(new ChunkGeneratorRunnable(applicationProperties));
            logger.info("Stated chunk generator.");
            for (int i = 0; i < applicationProperties.getNumberOfChunks(); i++) {
                submitTask(new ChunkProcessor(applicationProperties));
            }
            logger.info("Started chunk processor.");
//            TradeProcessorService tradeProcessorService = new TradeProcessorService(applicationProperties);
//            tradeProcessorService.submitTrade();
//            logger.info("Started trade processor.");
        } catch (IOException e) {
            logger.warning("File parsing failed...");
        }finally {
            chunkProcessorExecutorService.shutdown();
            chunkGeneratorExecutorService.shutdown();
        }
    }

    public long fileLineCounter(String path) throws IOException {
        long lineCount;
        try (Stream<String> stream = Files.lines(Path.of(path), StandardCharsets.UTF_8).parallel()) {
            lineCount = stream.count();
        }
        return lineCount - 1;
    }

    public String buildFilePath(int chunkNumber, String chunkFilePathWithName) {
        return chunkFilePathWithName + chunkNumber + ".csv";
    }

    @Override
    public void submitTask(ChunkProcessor chunkProcessor) {
        chunkProcessorExecutorService.submit(chunkProcessor);
    }
}
