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
ApplicationPropertiesUtils applicationPropertiesUtils;

    //TODO: #12 remove this applicationPropertiesUtils being passed in constructor @infinityjain
    public ChunkFileGeneratorService(ApplicationPropertiesUtils applicationPropertiesUtils){
     this.applicationPropertiesUtils =applicationPropertiesUtils;
    }

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
        long numOfLines = this.applicationPropertiesUtils.getTotalNoOfLines();
        String path = this.applicationPropertiesUtils.getFilePath();
        int chunksCount = this.applicationPropertiesUtils.getNumberOfChunks();
        int tempChunkCount = 1;
        long tempLineCount = 0;
        long linesCountPerFile = numOfLines / chunksCount;
        ChunkGeneratorAndProcessorService chunkGeneratorAndProcessorService = new ChunkGeneratorAndProcessorService();
        String chunkFilePath = chunkGeneratorAndProcessorService.buildFilePath(tempChunkCount, this.applicationPropertiesUtils.getChunkFilePathWithName());
        Files.createDirectories(Paths.get(this.applicationPropertiesUtils.getChunkDirectoryPath()));
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
                    chunkFilePath = chunkGeneratorAndProcessorService.buildFilePath(tempChunkCount, this.applicationPropertiesUtils.getChunkFilePathWithName());
                    writer = new BufferedWriter(new FileWriter(chunkFilePath));
                }
            }
            QueueDistributor.chunkQueue.put(chunkFilePath);
        } finally {
            writer.close();
        }
    }
}
