package io.reactivestax.utility;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.logging.Logger;

public class ApplicationPropertiesUtils {
    private final Logger logger = Logger.getLogger(ApplicationPropertiesUtils.class.getName());

    private long totalNoOfLines;
    private int numberOfChunks;
    private String filePath;
    private String chunkDirectoryPath;
    private String chunkFilePathWithName;
    private int maxRetryCount;
    private String dbName;
    private String portNumber;
    private String username;
    private String password;
    private int chunkProcessorThreadCount;
    private int tradeProcessorQueueCount;
    private int tradeProcessorThreadCount;
    private String tradeDistributionCriteria;
    private boolean tradeDistributionUseMap;
    private String tradeDistributionAlgorithm;

    public ApplicationPropertiesUtils(String applicationPropertiesFileName){
        loadApplicationProperties(applicationPropertiesFileName);
    }

    public void loadApplicationProperties(String applicationPropertiesFileName){
        Properties properties = new Properties();
        try (InputStream input = ApplicationPropertiesUtils.class.getClassLoader().getResourceAsStream(applicationPropertiesFileName)) {
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

    public long getTotalNoOfLines() {
        return totalNoOfLines;
    }

    public void setTotalNoOfLines(long totalNoOfLines) {
        this.totalNoOfLines = totalNoOfLines;
    }

    public int getNumberOfChunks() {
        return numberOfChunks;
    }

    public String getFilePath() {
        return filePath;
    }

    public String getChunkDirectoryPath() {
        return chunkDirectoryPath;
    }

    public String getChunkFilePathWithName() {
        return chunkFilePathWithName;
    }

    public int getMaxRetryCount() {
        return maxRetryCount;
    }

    public String getDbName() {
        return dbName;
    }

    public String getPortNumber() {
        return portNumber;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public int getChunkProcessorThreadCount() {
        return chunkProcessorThreadCount;
    }

    public int getTradeProcessorQueueCount() {
        return tradeProcessorQueueCount;
    }

    public int getTradeProcessorThreadCount() {
        return tradeProcessorThreadCount;
    }

    public String getTradeDistributionCriteria() {
        return tradeDistributionCriteria;
    }

    public boolean isTradeDistributionUseMap() {
        return tradeDistributionUseMap;
    }

    public String getTradeDistributionAlgorithm() {
        return tradeDistributionAlgorithm;
    }
}
