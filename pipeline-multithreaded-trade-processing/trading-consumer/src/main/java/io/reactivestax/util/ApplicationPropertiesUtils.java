package io.reactivestax.util;

import lombok.Getter;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.logging.Logger;

@Getter
public class ApplicationPropertiesUtils {
    private static ApplicationPropertiesUtils instance;
    private final Logger logger = Logger.getLogger(ApplicationPropertiesUtils.class.getName());

    private int maxRetryCount;
    private String dbDriverClass;
    private String dbUrl;
    private String dbUsername;
    private String dbPassword;
    private String hibernateDialect;
    private String hibernateDBCreationMode;
    private String queueHost;
    private String queueUsername;
    private String queuePassword;
    private String queueExchangeName;
    private String queueExchangeType;
    private String dlqName;
    private String retryQueueName;
    private int retryTTL;
    private int tradeProcessorQueueCount;
    private int tradeProcessorThreadCount;
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
            dbUsername = properties.getProperty("db.username");
            dbPassword = properties.getProperty("db.password");
            hibernateDialect = properties.getProperty("hibernate.dialect");
            hibernateDBCreationMode = properties.getProperty("hibernate.hbm2ddl.auto");
            dbDriverClass = properties.getProperty("db.driver.class");
            dbUrl = properties.getProperty("db.url");
            maxRetryCount = Integer.parseInt(properties.getProperty("max.retry.count"));
            tradeProcessorQueueCount = Integer.parseInt(properties.getProperty("queue.count"));
            tradeProcessorThreadCount = Integer.parseInt(properties.getProperty("trade.processor.thread.count"));
            queueHost = properties.getProperty("queue.host");
            queueUsername = properties.getProperty("queue.username");
            queuePassword = properties.getProperty("queue.password");
            queueExchangeName = properties.getProperty("queue.exchange.name");
            queueExchangeType = properties.getProperty("queue.exchange.type");
            persistenceTechnology = properties.getProperty("persistence.technology");
            messagingTechnology = properties.getProperty("messaging.technology");
            dlqName = properties.getProperty("dlq.name");
            retryQueueName = properties.getProperty("retry.queue.name");
            retryTTL = Integer.parseInt(properties.getProperty("retry.ttl"));
        } catch (IOException e) {
            logger.warning("File not found Exception.");
            System.exit(1);
        }
    }
}
