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
import java.util.logging.Logger;
import java.util.stream.Stream;

public class ChunkGeneratorAndProcessorService implements Submittable<ChunkProcessor> {
    private ExecutorService chunkProcessorExecutorService;
    Logger logger = Logger.getLogger(ChunkGeneratorAndProcessorService.class.getName());

    public void setupDataSourceAndStartGeneratorsAndProcessors() {
        logger.info("Setting up database and project dependencies.");
        ExecutorService chunkGeneratorExecutorService;
        try {
            String path = MaintainStaticValues.getFilePath();
            logger.info("Counting total number of lines in the file");
            long numOfLines = fileLineCounter(path);
            MaintainStaticValues.setRowsPerFile(numOfLines);
            HikariDataSource hikariDataSource = DatabaseConnection.configureHikariCP(
                    MaintainStaticValues.getPortNumber(),
                    MaintainStaticValues.getDbName(),
                    MaintainStaticValues.getUsername(),
                    MaintainStaticValues.getPassword()
            );
            QueueDistributor.initializeQueue();
            chunkGeneratorExecutorService = Executors.newSingleThreadExecutor();
            chunkProcessorExecutorService =
                    Executors.newFixedThreadPool(MaintainStaticValues.getChunkProcessorThreadCount());
            chunkGeneratorExecutorService.submit(new ChunkGeneratorRunnable());
            logger.info("Stated chunk generator.");
            for (int i = 0; i < MaintainStaticValues.getNumberOfChunks(); i++) {
                submitTask(new ChunkProcessor(hikariDataSource));
            }
            logger.info("Started chunk processor.");
            TradeProcessorService tradeProcessorService = new TradeProcessorService();
            tradeProcessorService.submitTrade(hikariDataSource);
            logger.info("Started trade processor.");
        } catch (IOException e) {
            logger.warning("File parsing failed...");
        }finally {
            chunkProcessorExecutorService.shutdown();
            chunkProcessorExecutorService.shutdown();
        }
    }

    public void setStaticValues() {
        Properties properties = new Properties();
        try (InputStream input = ChunkGeneratorAndProcessorService.class.getClassLoader().getResourceAsStream("application.properties")) {
            if (input == null) {
                logger.warning("Sorry, unable to find application.properties");
                System.exit(1);
            }
            properties.load(input);
            MaintainStaticValues.setFilePath(properties.getProperty("file.path"));
            MaintainStaticValues.setChunkDirectoryPath(properties.getProperty("chunk.directory.path"));
            MaintainStaticValues.setChunkFilePathWithName(properties.getProperty("chunk.file.path"));
            MaintainStaticValues.setDbName(properties.getProperty("db.name"));
            MaintainStaticValues.setUsername(properties.getProperty("username"));
            MaintainStaticValues.setPassword(properties.getProperty("password"));
            MaintainStaticValues.setPortNumber(properties.getProperty("port"));
            MaintainStaticValues.setNumberOfChunks(Integer.parseInt(properties.getProperty("chunks.count")));
            MaintainStaticValues.setMaxRetryCount(Integer.parseInt(properties.getProperty("max.retry.count")));
            MaintainStaticValues.setChunkProcessorThreadCount(Integer.parseInt(properties.getProperty("chunk.processor.thread.count")));
            MaintainStaticValues.setTradeProcessorQueueCount(Integer.parseInt(properties.getProperty("queue.count")));
            MaintainStaticValues.setTradeProcessorThreadCount(Integer.parseInt(properties.getProperty("trade.processor.thread.count")));
            MaintainStaticValues.setTradeDistributionCriteria(properties.getProperty("trade.distribution.criteria"));
        } catch (IOException e) {
            logger.warning("File not found Exception.");
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

    public String buildFilePath(int chunkNumber) {
        return MaintainStaticValues.getChunkFilePathWithName() + chunkNumber + ".csv";
    }

    @Override
    public void submitTask(ChunkProcessor chunkProcessor) {
        chunkProcessorExecutorService.submit(chunkProcessor);
    }
}
