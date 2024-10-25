package io.reactivestax.util;

import lombok.Getter;
import lombok.Setter;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.logging.Logger;

@Getter
public class ApplicationPropertiesUtils {
    private static ApplicationPropertiesUtils instance;
    private final Logger logger = Logger.getLogger(ApplicationPropertiesUtils.class.getName());

    @Setter
    private long totalNoOfLines;
    private int numberOfChunks;
    private String filePath;
    private String chunkDirectoryPath;
    private String chunkFilePathWithName;
    private String hibernateDialect;
    private String hibernateDBCreationMode;
    private String dbDriverClass;
    private String dbUrl;
    private String dbUsername;
    private String dbPassword;
    private String queueHost;
    private String queueUsername;
    private String queuePassword;
    private String queueExchangeName;
    private String queueExchangeType;
    private int chunkProcessorThreadCount;
    private int tradeProcessorQueueCount;
    private String tradeDistributionCriteria;
    private boolean tradeDistributionUseMap;
    private String tradeDistributionAlgorithm;
    private String persistenceTechnology;
    private String messagingTechnology;

    private ApplicationPropertiesUtils(String applicationPropertiesFileName) {
        loadApplicationProperties(applicationPropertiesFileName);
    }

    public static synchronized ApplicationPropertiesUtils getInstance(String applicationPropertiesFileName) {
        if (instance == null) {
            instance = new ApplicationPropertiesUtils(applicationPropertiesFileName);
        }
        return instance;
    }

    public static synchronized ApplicationPropertiesUtils getInstance() {
        if (instance == null) {
            instance = new ApplicationPropertiesUtils("application.properties");
        }
        return instance;
    }

    public void loadApplicationProperties(String applicationPropertiesFileName) {
        Properties properties = new Properties();
        try (InputStream input = ApplicationPropertiesUtils.class.getClassLoader()
                .getResourceAsStream(applicationPropertiesFileName)) {
            if (input == null) {
                logger.warning("Sorry, unable to find application.properties");
                System.exit(1);
            }
            properties.load(input);
            filePath = properties.getProperty("file.path");
            chunkDirectoryPath = properties.getProperty("chunk.directory.path");
            chunkFilePathWithName = properties.getProperty("chunk.file.path");
            hibernateDialect = properties.getProperty("hibernate.dialect");
            hibernateDBCreationMode = properties.getProperty("hibernate.hbm2ddl.auto");
            dbDriverClass = properties.getProperty("db.driver.class");
            dbUrl = properties.getProperty("db.url");
            dbUsername = properties.getProperty("username");
            dbPassword = properties.getProperty("password");
            numberOfChunks = Integer.parseInt(properties.getProperty("chunks.count"));
            chunkProcessorThreadCount = Integer.parseInt(properties.getProperty("chunk.processor.thread.count"));
            tradeProcessorQueueCount = Integer.parseInt(properties.getProperty("queue.count"));
            tradeDistributionCriteria = properties.getProperty("trade.distribution.criteria");
            tradeDistributionUseMap = Boolean.parseBoolean(properties.getProperty("trade.distribution.use.map"));
            tradeDistributionAlgorithm = properties.getProperty("trade.distribution.algorithm");
            queueHost = properties.getProperty("queue.host");
            queueUsername = properties.getProperty("queue.username");
            queuePassword = properties.getProperty("queue.password");
            queueExchangeName = properties.getProperty("queue.exchange.name");
            queueExchangeType = properties.getProperty("queue.exchange.type");
            persistenceTechnology = properties.getProperty("persistence.technology");
            messagingTechnology = properties.getProperty("messaging.technology");
        } catch (IOException e) {
            logger.warning("File not found Exception.");
            System.exit(1);
        }
    }
}
