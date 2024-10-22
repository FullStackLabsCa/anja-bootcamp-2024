package io.reactivestax.service;

import io.reactivestax.util.ApplicationPropertiesUtils;
import io.reactivestax.util.messaging.Submittable;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Logger;
import java.util.stream.Stream;

public class TradeService implements Submittable<ChunkFileProcessorService> {
    private ExecutorService chunkGeneratorExecutorService;
    private ExecutorService chunkProcessorExecutorService;
    Logger logger = Logger.getLogger(TradeService.class.getName());

    public void startTradeProducer() {
        ApplicationPropertiesUtils applicationProperties = ApplicationPropertiesUtils.getInstance();
        try {
            String path = applicationProperties.getFilePath();
            long numOfLines = fileLineCounter(path);
            logger.info("Counting total number of lines in the file");
            applicationProperties.setTotalNoOfLines(numOfLines);
            chunkGeneratorExecutorService = Executors.newSingleThreadExecutor();
            chunkProcessorExecutorService =
                    Executors.newFixedThreadPool(applicationProperties.getChunkProcessorThreadCount());
            chunkGeneratorExecutorService.submit(new ChunkFileGeneratorService());
            logger.info("Stated chunk generator.");
            for (int i = 0; i < applicationProperties.getNumberOfChunks(); i++) {
                submitTask(new ChunkFileProcessorService());
            }
            logger.info("Started chunk processor.");
        } catch (IOException e) {
            logger.warning("File parsing failed...");
        } finally {
            chunkGeneratorExecutorService.shutdown();
            chunkProcessorExecutorService.shutdown();
        }
    }

    public void startTradeConsumer() {
        TradeProcessorSubmitterService tradeProcessorSubmitterService = new TradeProcessorSubmitterService();
        tradeProcessorSubmitterService.submitTrade();
        logger.info("Started trade processor.");
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
    public void submitTask(ChunkFileProcessorService chunkProcessor) {
        chunkProcessorExecutorService.submit(chunkProcessor);
    }
}
