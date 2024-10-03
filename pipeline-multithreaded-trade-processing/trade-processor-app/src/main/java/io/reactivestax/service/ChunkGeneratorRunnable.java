package io.reactivestax.service;

import io.reactivestax.utility.MaintainStaticValues;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.logging.Logger;

public class ChunkGeneratorRunnable implements Runnable, ChunkGenerator{
Logger logger = Logger.getLogger(ChunkGeneratorRunnable.class.getName());


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
        long numOfLines = MaintainStaticValues.getTotalNoOfLines();
        String path = MaintainStaticValues.getFilePath();
        int chunksCount = MaintainStaticValues.getNumberOfChunks();
        int tempChunkCount = 1;
        long tempLineCount = 0;
        long linesCountPerFile = numOfLines / chunksCount;
        ChunkGeneratorAndProcessorService chunkGeneratorAndProcessorService = new ChunkGeneratorAndProcessorService();
        String chunkFilePath = chunkGeneratorAndProcessorService.buildFilePath(tempChunkCount);
        Files.createDirectories(Paths.get(MaintainStaticValues.getChunkDirectoryPath()));
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
                    chunkFilePath = chunkGeneratorAndProcessorService.buildFilePath(tempChunkCount);
                    writer = new BufferedWriter(new FileWriter(chunkFilePath));
                }
            }
            QueueDistributor.chunkQueue.put(chunkFilePath);
        } finally {
            writer.close();
        }
    }
}
