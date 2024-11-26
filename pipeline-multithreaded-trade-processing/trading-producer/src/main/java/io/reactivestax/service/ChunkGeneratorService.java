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
import java.nio.file.Paths;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;

public class ChunkGeneratorService implements ChunkGenerator {

    private static ChunkGeneratorService instance;

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
        Files.createDirectories(Paths.get(applicationPropertiesUtils.getChunkDirectoryPath()));
        readAndWriteToChunk(path, chunksCount, tradeService, applicationPropertiesUtils, numOfLinesPerFile);
    }

    private void readAndWriteToChunk(String path, int chunksCount, TradeService tradeService, ApplicationPropertiesUtils applicationPropertiesUtils, long numOfLinesPerFile) throws IOException, InterruptedException {
        try (BufferedReader reader =
                     Files.newBufferedReader(Path.of(path), StandardCharsets.UTF_8)) {
            AtomicReference<String> line = new AtomicReference<>(reader.readLine());
            for (int i = 1; i <= chunksCount; i++) {
                String chunkFilePath = tradeService.buildFilePath(i, applicationPropertiesUtils.getChunkFilePathWithName());
                writeToChunk(numOfLinesPerFile, chunkFilePath, line, reader);
            }
        }
    }

    private void writeToChunk(long numOfLinesPerFile, String chunkFilePath, AtomicReference<String> line, BufferedReader reader) throws IOException, InterruptedException {
        try (BufferedWriter writer = Files.newBufferedWriter(Path.of(chunkFilePath))) {
            for (int i = 0; i < numOfLinesPerFile; i++) {
                line.set(reader.readLine());
                writer.write(line.get() != null ? line.get() : "");
                writer.newLine();
            }
            QueueProvider.getInstance().getChunkQueue().put(chunkFilePath);
        }
    }
}
