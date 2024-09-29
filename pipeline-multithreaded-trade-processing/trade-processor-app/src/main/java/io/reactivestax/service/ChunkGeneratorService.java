package io.reactivestax.service;

import com.zaxxer.hikari.HikariDataSource;
import io.reactivestax.database.DatabaseConnection;
import io.reactivestax.utility.MaintainStaticValues;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Stream;

public class ChunkGeneratorService implements ChunkGenerator, Submittable<ChunkProcessor> {
    private final ExecutorService chunkGeneratorExecutorService = Executors.newFixedThreadPool(10);
    private HikariDataSource hikariDataSource;

    public void setupDataSourceAndStartGeneratorAndProcessor() {
        try {
            setStaticValues();
            String path = MaintainStaticValues.getFilePath();
            long numOfLines = fileLineCounter(path);
            MaintainStaticValues.setRowsPerFile(numOfLines);
            hikariDataSource = DatabaseConnection.configureHikariCP(MaintainStaticValues.getPortNumber(),
                    MaintainStaticValues.getDbName(),
                    MaintainStaticValues.getUsername(),
                    MaintainStaticValues.getPassword());
            generateChunks(numOfLines, path);
            TradeProcessorService tradeProcessorService = new TradeProcessorService();
            tradeProcessorService.submitTrade(hikariDataSource);
        } catch (IOException e) {
            System.out.println("File parsing failed...");
        }
    }

    public void setStaticValues() {
        Properties properties = new Properties();
        try (InputStream input = ChunkGeneratorService.class.getClassLoader().getResourceAsStream("application.properties")) {
            if (input == null) {
                System.out.println("Sorry, unable to find application.properties");
                System.exit(1);
            }
            properties.load(input);
            MaintainStaticValues.setFilePath(properties.getProperty("file.path"));
            MaintainStaticValues.setChunkFilePath(properties.getProperty("chunk.file.path"));
            MaintainStaticValues.setDbName(properties.getProperty("db.name"));
            MaintainStaticValues.setUsername(properties.getProperty("username"));
            MaintainStaticValues.setPassword(properties.getProperty("password"));
            MaintainStaticValues.setPortNumber(properties.getProperty("port"));
            MaintainStaticValues.setNumberOfChunks(Integer.parseInt(properties.getProperty("number.of.chunks")));
            MaintainStaticValues.setMaxRetryCount(Integer.parseInt(properties.getProperty("max.retry.count")));
        } catch (IOException e) {
            System.out.println("File not found Exception.");
            System.exit(1);
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
    public void generateChunks(long numOfLines, String path) throws IOException {
        int chunksCount = MaintainStaticValues.getNumberOfChunks();
        int tempChunkCount = 1;
        long tempLineCount = 0;
        long linesCountPerFile = numOfLines / chunksCount;
        String chunkFilePath = buildFilePath(tempChunkCount);
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
                    submitTask(new ChunkProcessor(chunkFilePath, hikariDataSource));
                    chunkFilePath = buildFilePath(tempChunkCount);
                    writer = new BufferedWriter(new FileWriter(chunkFilePath));
                }
            }
            submitTask(new ChunkProcessor(chunkFilePath, hikariDataSource));
        } finally {
            writer.close();
            chunkGeneratorExecutorService.shutdown();
        }
    }

    public String buildFilePath(int chunkNumber) {
        return MaintainStaticValues.getChunkFilePath() + chunkNumber + ".csv";
    }

    @Override
    public void submitTask(ChunkProcessor chunkProcessor) {
        chunkGeneratorExecutorService.submit(chunkProcessor);
    }
}
