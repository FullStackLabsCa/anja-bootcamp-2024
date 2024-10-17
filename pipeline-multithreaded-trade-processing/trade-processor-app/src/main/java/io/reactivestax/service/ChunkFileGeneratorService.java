package io.reactivestax.service;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.logging.Logger;

import io.reactivestax.utility.ApplicationPropertiesUtils;
import io.reactivestax.utility.messaging.QueueDistributor;

public class ChunkFileGeneratorService implements Runnable, ChunkGeneratorService {
Logger logger = Logger.getLogger(ChunkFileGeneratorService.class.getName());

    @Override
    public void run(){
        try {
            generateChunks();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } catch (IOException e) {
            logger.warning("IO Exception.");
        }
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
        ChunkGeneratorAndProcessorService chunkGeneratorAndProcessorService = new ChunkGeneratorAndProcessorService();
        String chunkFilePath = chunkGeneratorAndProcessorService.buildFilePath(tempChunkCount, applicationPropertiesUtils.getChunkFilePathWithName());
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
                    QueueDistributor.chunkQueue.put(chunkFilePath);
                    chunkFilePath = chunkGeneratorAndProcessorService.buildFilePath(tempChunkCount, applicationPropertiesUtils.getChunkFilePathWithName());
                    writer = new BufferedWriter(new FileWriter(chunkFilePath));
                }
            }
            QueueDistributor.chunkQueue.put(chunkFilePath);
        } finally {
            writer.close();
        }
    }
}
