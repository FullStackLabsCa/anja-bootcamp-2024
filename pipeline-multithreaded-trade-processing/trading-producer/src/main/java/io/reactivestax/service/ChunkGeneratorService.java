package io.reactivestax.service;

import io.reactivestax.util.ApplicationPropertiesUtils;
import io.reactivestax.util.QueueProvider;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;

public class ChunkGeneratorService implements ChunkGenerator {

    private static ChunkGeneratorService instance;

    private ChunkGeneratorService() {
    }

    public static synchronized ChunkGeneratorService getInstance() {
        if (instance == null) {
            instance = new ChunkGeneratorService();
        }

        return instance;
    }

    @Override
    public void generateChunks() throws IOException, InterruptedException {
        ApplicationPropertiesUtils applicationPropertiesUtils = ApplicationPropertiesUtils.getInstance();
        long numOfLines = applicationPropertiesUtils.getTotalNoOfLines();
        String path = applicationPropertiesUtils.getFilePath();
        int chunksCount = applicationPropertiesUtils.getNumberOfChunks();
        int tempChunkCount = 1;
        long tempLineCount = 0;
        long linesCountPerFile = numOfLines / chunksCount;
        TradeService tradeService = TradeService.getInstance();
        String chunkFilePath = tradeService.buildFilePath(tempChunkCount, applicationPropertiesUtils.getChunkFilePathWithName());
        Files.createDirectories(Paths.get(applicationPropertiesUtils.getChunkDirectoryPath()));
        BufferedWriter writer = new BufferedWriter(new FileWriter(chunkFilePath));
        try (BufferedReader reader = new BufferedReader(new FileReader(path))) {
            String line = reader.readLine();
            while ((line = reader.readLine()) != null) {
                writer.write(line);
                writer.newLine();
                tempLineCount++;
                if (tempLineCount == linesCountPerFile && tempChunkCount != chunksCount) {
                    tempChunkCount++;
                    tempLineCount = 0;
                    writer.close();
                    QueueProvider.getChunkQueue().put(chunkFilePath);
                    chunkFilePath = tradeService.buildFilePath(tempChunkCount, applicationPropertiesUtils.getChunkFilePathWithName());
                    writer = new BufferedWriter(new FileWriter(chunkFilePath));
                }
            }
            QueueProvider.getChunkQueue().put(chunkFilePath);
        } finally {
            writer.close();
        }
    }
}
