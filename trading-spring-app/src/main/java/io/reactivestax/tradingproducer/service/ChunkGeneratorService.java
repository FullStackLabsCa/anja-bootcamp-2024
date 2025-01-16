package io.reactivestax.tradingproducer.service;

import io.reactivestax.tradingproducer.task.ChunkGenerator;
import io.reactivestax.tradingproducer.util.ApplicationPropertiesUtils;
import io.reactivestax.tradingproducer.util.QueueProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.atomic.AtomicReference;

@Service
public class ChunkGeneratorService implements ChunkGenerator {

    private final ApplicationPropertiesUtils applicationPropertiesUtils;
    private final TradeService tradeService;
    private final QueueProvider queueProvider;

    @Autowired
    public ChunkGeneratorService(ApplicationPropertiesUtils applicationPropertiesUtils, TradeService tradeService,
                                 QueueProvider queueProvider) {
        this.applicationPropertiesUtils = applicationPropertiesUtils;
        this.tradeService = tradeService;
        this.queueProvider = queueProvider;
    }

    @Override
    public void generateChunks() throws IOException, InterruptedException {
        long numOfLines = applicationPropertiesUtils.getTotalNoOfLines();
        String path = applicationPropertiesUtils.getFilePath();
        int chunksCount = applicationPropertiesUtils.getNumberOfChunks();
        long numOfLinesPerFile = Math.round((float) numOfLines / chunksCount);
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
            queueProvider.getChunkQueue().put(chunkFilePath);
        }
    }
}
