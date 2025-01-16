package io.reactivestax.tradingproducer.service;

import io.reactivestax.tradingproducer.task.ChunkFileGenerator;
import io.reactivestax.tradingproducer.task.ChunkFileProcessor;
import io.reactivestax.tradingproducer.util.ApplicationPropertiesUtils;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Logger;
import java.util.stream.IntStream;
import java.util.stream.Stream;

@Service
public class TradeService {
    private final ApplicationPropertiesUtils applicationProperties;
    private final ObjectProvider<ChunkFileGenerator> chunkFileGeneratorObjectProvider;
    private final ObjectProvider<ChunkFileProcessor> chunkFileProcessorObjectProvider;
    private ExecutorService chunkGeneratorExecutorService;
    private ExecutorService chunkProcessorExecutorService;
    Logger logger = Logger.getLogger(TradeService.class.getName());

    @Autowired
    public TradeService(ApplicationPropertiesUtils applicationPropertiesUtils,
                        ObjectProvider<ChunkFileGenerator> chunkFileGeneratorObjectProvider,
                        ObjectProvider<ChunkFileProcessor> chunkFileProcessorObjectProvider) {
        this.applicationProperties = applicationPropertiesUtils;
        this.chunkFileGeneratorObjectProvider = chunkFileGeneratorObjectProvider;
        this.chunkFileProcessorObjectProvider = chunkFileProcessorObjectProvider;
    }

    public void startTradeProducer() {
        try {
            String path = applicationProperties.getFilePath();
            long numOfLines = fileLineCounter(path);
            logger.info("Counting total number of lines in the file");
            applicationProperties.setTotalNoOfLines(numOfLines);
            chunkGeneratorExecutorService = Executors.newSingleThreadExecutor();
            chunkProcessorExecutorService =
                    Executors.newFixedThreadPool(applicationProperties.getChunkProcessorThreadCount());
            ChunkFileGenerator chunkFileGenerator = chunkFileGeneratorObjectProvider.getObject();
            chunkGeneratorExecutorService.submit(chunkFileGenerator);
            logger.info("Stated chunk generator.");
            IntStream.range(0, applicationProperties.getNumberOfChunks()).forEach(i -> {
                ChunkFileProcessor chunkFileProcessor = chunkFileProcessorObjectProvider.getObject();
                chunkProcessorExecutorService.submit(chunkFileProcessor);
            });
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
