package io.reactivestax.service;

import io.reactivestax.utility.ApplicationPropertiesUtils;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.logging.Logger;

public class FileChunkGeneratorService implements Runnable, ChunkGeneratorService {
Logger logger = Logger.getLogger(FileChunkGeneratorService.class.getName());
ApplicationPropertiesUtils applicationPropertiesUtils;

    public FileChunkGeneratorService(ApplicationPropertiesUtils applicationPropertiesUtils){
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
