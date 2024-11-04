package io.reactivestax.service;

import com.rabbitmq.client.UnblockedCallback;
import io.reactivestax.task.ChunkFileGenerator;
import io.reactivestax.task.ChunkFileProcessor;
import io.reactivestax.util.ApplicationPropertiesUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Supplier;
import java.util.logging.Logger;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class TradeService {
    private static TradeService instance;
    private ExecutorService chunkGeneratorExecutorService;
    private ExecutorService chunkProcessorExecutorService;
    Logger logger = Logger.getLogger(TradeService.class.getName());

    private TradeService() {
    }

    public static synchronized TradeService getInstance() {
        if (instance == null) {
            Supplier<TradeService> tradeServiceSupplier = TradeService::new;
            instance = tradeServiceSupplier.get();
        }

        return instance;
    }

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
            chunkGeneratorExecutorService.submit(new ChunkFileGenerator());
            logger.info("Stated chunk generator.");
            IntStream.range(0, applicationProperties.getNumberOfChunks()).forEach(i -> chunkProcessorExecutorService.submit(new ChunkFileProcessor()));
            logger.info("Started chunk processor.");
            addShutdownHook();
        } catch (IOException e) {
            logger.warning("File parsing failed...");
        }
    }

    private void addShutdownHook() {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            logger.info("Shutdown signal received. Stopping consumer...");
            chunkGeneratorExecutorService.shutdownNow();
            chunkProcessorExecutorService.shutdownNow();
            logger.info("Consumer stopped.");
        }));
    }

    public long fileLineCounter(String path) throws IOException {
        long lineCount;
        try (Stream<String> stream = Files.lines(Path.of(path), StandardCharsets.UTF_8).parallel()) {
            lineCount = stream.count() - 1;
        }
        return lineCount;
    }

    public String buildFilePath(int chunkNumber, String chunkFilePathWithName) {
        return chunkFilePathWithName + chunkNumber + ".csv";
    }
}
