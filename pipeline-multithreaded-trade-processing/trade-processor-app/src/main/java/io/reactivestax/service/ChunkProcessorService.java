package io.reactivestax.service;

import com.zaxxer.hikari.HikariDataSource;
import io.reactivestax.database.DatabaseConnection;
import io.reactivestax.utility.MaintainStaticCounts;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Stream;

public class ChunkProcessorService implements ChunkGeneratorInterface, SubmitTaskInterface<ChunkProcessor> {
    private final ExecutorService chunkGeneratorExecutorService = Executors.newFixedThreadPool(10);
    private HikariDataSource hikariDataSource;

    public void processTrade(String path) {
        try {
            long numOfLines = fileLineCounter(path);
            MaintainStaticCounts.setRowsPerFile(numOfLines);
            hikariDataSource = DatabaseConnection.configureHikariCP("3306", "trade_processor", "password123");
            chunkGenerator(numOfLines, path);
            TradeProcessorService tradeProcessorService = new TradeProcessorService();
            tradeProcessorService.submitTrade(hikariDataSource);
        } catch (IOException e) {
            System.out.println("File parsing failed...");
        }
    }

    public long fileLineCounter(String path) throws IOException {
        long lineCount;
        try (Stream<String> stream = Files.lines(Path.of(path), StandardCharsets.UTF_8).parallel()) {
            lineCount = stream.count();
        }
        return lineCount - 1;
    }

    @Override
    public void chunkGenerator(long numOfLines, String path) throws IOException {
        int chunksCount = 10;
        int tempChunkCount = 1;
        long tempLineCount = 0;
        MaintainStaticCounts.setNumberOfChunks(chunksCount);
        long linesCountPerFile = numOfLines / chunksCount;
        String chunkFilePath = buildFilePath(tempChunkCount);
        BufferedWriter writer = new BufferedWriter(new FileWriter(chunkFilePath, true));
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
                    submitTask(new ChunkProcessor(chunkFilePath, hikariDataSource));
                    chunkFilePath = buildFilePath(tempChunkCount);
                    writer = new BufferedWriter(new FileWriter(chunkFilePath, true));
                }
            }
            submitTask(new ChunkProcessor(chunkFilePath, hikariDataSource));
        } finally {
            writer.close();
            chunkGeneratorExecutorService.shutdown();
        }
    }

    public String buildFilePath(int chunkNumber) {
        return "/Users/Anant.Jain/source/student/boca-bc24-java-core-problems/src" +
                "/problems/jdbc/tradeprocessor/assets/chunks/trade_records_chunk" + chunkNumber + ".csv";
    }

    @Override
    public void submitTask(ChunkProcessor chunkProcessor) {
        chunkGeneratorExecutorService.submit(chunkProcessor);
    }
}
