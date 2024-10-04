package io.reactivestax.utility;

import io.reactivestax.service.ChunkGeneratorAndProcessorService;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.logging.Logger;

public class ApplicationPropertiesUtils {
    static Logger logger = Logger.getLogger(ApplicationPropertiesUtils.class.getName());

    static long totalNoOfLines = 0;
    static int numberOfChunks = 0;
    static String filePath = "";
    static String chunkDirectoryPath = "";
    static String chunkFilePathWithName = "";
    static int maxRetryCount = 0;
    static String dbName = "";
    static String portNumber = "";
    static String username = "";
    static String password = "";
    static int chunkProcessorThreadCount = 0;
    static int tradeProcessorQueueCount = 0;
    static int tradeProcessorThreadCount = 0;
    static String tradeDistributionCriteria = "";
    static boolean tradeDistributionUseMap = false;
    static String tradeDistributionAlgorithm = "";

    private ApplicationPropertiesUtils(){}

    public static void loadApplicationProperties(){
        Properties properties = new Properties();
        try (InputStream input = ChunkGeneratorAndProcessorService.class.getClassLoader().getResourceAsStream("application.properties")) {
            if (input == null) {
                logger.warning("Sorry, unable to find application.properties");
                System.exit(1);
            }
            properties.load(input);
            filePath = properties.getProperty("file.path");
            chunkDirectoryPath = properties.getProperty("chunk.directory.path");
            chunkFilePathWithName = properties.getProperty("chunk.file.path");
            dbName = properties.getProperty("db.name");
            username = properties.getProperty("username");
            password = properties.getProperty("password");
            portNumber = properties.getProperty("port");
            numberOfChunks = Integer.parseInt(properties.getProperty("chunks.count"));
            maxRetryCount = Integer.parseInt(properties.getProperty("max.retry.count"));
            chunkProcessorThreadCount = Integer.parseInt(properties.getProperty("chunk.processor.thread.count"));
            tradeProcessorQueueCount = Integer.parseInt(properties.getProperty("queue.count"));
            tradeProcessorThreadCount = Integer.parseInt(properties.getProperty("trade.processor.thread.count"));
            tradeDistributionCriteria = properties.getProperty("trade.distribution.criteria");
            tradeDistributionUseMap = Boolean.parseBoolean(properties.getProperty("trade.distribution.use.map"));
            tradeDistributionAlgorithm = properties.getProperty("trade.distribution.algorithm");
        } catch (IOException e) {
            logger.warning("File not found Exception.");
            System.exit(1);
        }
    }

    public static long getTotalNoOfLines() {
        return totalNoOfLines;
    }

    public static void setTotalNoOfLines(long totalNoOfLines) {
        ApplicationPropertiesUtils.totalNoOfLines = totalNoOfLines;
    }

    public static int getNumberOfChunks() {
        return numberOfChunks;
    }

    public static void setNumberOfChunks(int numberOfChunks) {
        ApplicationPropertiesUtils.numberOfChunks = numberOfChunks;
    }

    public static String getFilePath() {
        return filePath;
    }

    public static void setFilePath(String filePath) {
        ApplicationPropertiesUtils.filePath = filePath;
    }

    public static String getChunkDirectoryPath() {
        return chunkDirectoryPath;
    }

    public static void setChunkDirectoryPath(String chunkDirectoryPath) {
        ApplicationPropertiesUtils.chunkDirectoryPath = chunkDirectoryPath;
    }

    public static String getChunkFilePathWithName() {
        return chunkFilePathWithName;
    }

    public static void setChunkFilePathWithName(String chunkFilePathWithName) {
        ApplicationPropertiesUtils.chunkFilePathWithName = chunkFilePathWithName;
    }

    public static int getMaxRetryCount() {
        return maxRetryCount;
    }

    public static String getDbName() {
        return dbName;
    }

    public static String getPortNumber() {
        return portNumber;
    }

    public static void setPortNumber(String portNumber) {
        ApplicationPropertiesUtils.portNumber = portNumber;
    }

    public static String getUsername() {
        return username;
    }

    public static String getPassword() {
        return password;
    }

    public static int getChunkProcessorThreadCount() {
        return chunkProcessorThreadCount;
    }

    public static void setChunkProcessorThreadCount(int chunkProcessorThreadCount) {
        ApplicationPropertiesUtils.chunkProcessorThreadCount = chunkProcessorThreadCount;
    }

    public static int getTradeProcessorQueueCount() {
        return tradeProcessorQueueCount;
    }

    public static void setTradeProcessorQueueCount(int tradeProcessorQueueCount) {
        ApplicationPropertiesUtils.tradeProcessorQueueCount = tradeProcessorQueueCount;
    }

    public static int getTradeProcessorThreadCount() {
        return tradeProcessorThreadCount;
    }

    public static String getTradeDistributionCriteria() {
        return tradeDistributionCriteria;
    }

    public static void setTradeDistributionCriteria(String tradeDistributionCriteria) {
        ApplicationPropertiesUtils.tradeDistributionCriteria = tradeDistributionCriteria;
    }

    public static boolean isTradeDistributionUseMap() {
        return tradeDistributionUseMap;
    }

    public static void setTradeDistributionUseMap(boolean tradeDistributionUseMap) {
        ApplicationPropertiesUtils.tradeDistributionUseMap = tradeDistributionUseMap;
    }

    public static String getTradeDistributionAlgorithm() {
        return tradeDistributionAlgorithm;
    }

    public static void setTradeDistributionAlgorithm(String tradeDistributionAlgorithm) {
        ApplicationPropertiesUtils.tradeDistributionAlgorithm = tradeDistributionAlgorithm;
    }
}
