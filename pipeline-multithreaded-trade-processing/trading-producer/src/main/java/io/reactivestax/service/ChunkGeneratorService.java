package io.reactivestax.service;

import io.reactivestax.task.ChunkGenerator;
import io.reactivestax.util.ApplicationPropertiesUtils;
import io.reactivestax.util.QueueProvider;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;
import java.util.logging.Logger;
import java.util.stream.IntStream;
import java.util.stream.LongStream;

public class ChunkGeneratorService implements ChunkGenerator {

    private static ChunkGeneratorService instance;
    Logger logger = Logger.getLogger(ChunkGeneratorService.class.getName());

    private ChunkGeneratorService() {
    }

    public static synchronized ChunkGeneratorService getInstance() {
        if (instance == null) {
            Supplier<ChunkGeneratorService> chunkGeneratorServiceSupplier = ChunkGeneratorService::new;
            instance = chunkGeneratorServiceSupplier.get();
        }

        return instance;
    }

    @Override
    public void generateChunks() throws IOException, InterruptedException {
        ApplicationPropertiesUtils applicationPropertiesUtils = ApplicationPropertiesUtils.getInstance();
        long numOfLines = applicationPropertiesUtils.getTotalNoOfLines();
        String path = applicationPropertiesUtils.getFilePath();
        int chunksCount = applicationPropertiesUtils.getNumberOfChunks();
        long numOfLinesPerFile = Math.round((float) numOfLines / chunksCount);
        TradeService tradeService = TradeService.getInstance();
        try (BufferedReader reader =
                     Files.newBufferedReader(Path.of(path), StandardCharsets.UTF_8)) {
            AtomicReference<String> line = new AtomicReference<>(reader.readLine());
            IntStream.range(1, chunksCount + 1).forEach(fileNumber -> {
                String chunkFilePath = tradeService.buildFilePath(fileNumber,
                        applicationPropertiesUtils.getChunkFilePathWithName());
                try (BufferedWriter writer = Files.newBufferedWriter(Path.of(chunkFilePath))) {
                    LongStream.range(0, numOfLinesPerFile).forEach(lineNumber -> {
                        try {
                            line.set(reader.readLine());
                            writer.write(line.get() != null ? line.get() : "");
                            writer.newLine();
                        } catch (IOException e) {
                            logger.warning("IO Exception.");
                        }
                    });
                    QueueProvider.getInstance().getChunkQueue().put(chunkFilePath);
                } catch (IOException | InterruptedException e) {
                    logger.warning("Exception while creating chunks.");
                    Thread.currentThread().interrupt();
                }
            });
        }
    }
}
