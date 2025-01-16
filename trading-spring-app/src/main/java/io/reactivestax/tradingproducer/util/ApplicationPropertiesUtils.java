package io.reactivestax.tradingproducer.util;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.logging.Logger;

@Getter
@Component
public class ApplicationPropertiesUtils {
    private final Logger logger = Logger.getLogger(ApplicationPropertiesUtils.class.getName());

    @Setter
    private long totalNoOfLines;
    @Value("${chunks.count}")
    private int numberOfChunks;
    @Value("${file.path}")
    private String filePath;
    @Value("${chunk.directory.path}")
    private String chunkDirectoryPath;
    @Value("${chunk.file.path}")
    private String chunkFilePathWithName;
    @Value("${db.driver.class}")
    private String dbDriverClass;
    @Value("${db.url}")
    private String dbUrl;
    @Value("${db.username}")
    private String dbUsername;
    @Value("${db.password}")
    private String dbPassword;
    @Value("${chunk.processor.thread.count}")
    private int chunkProcessorThreadCount;
    @Value("${queue.count}")
    private int tradeProcessorQueueCount;
    @Value("${trade.distribution.criteria}")
    private String tradeDistributionCriteria;
    @Value("${trade.distribution.use.map}")
    private boolean tradeDistributionUseMap;
    @Value("${trade.distribution.algorithm}")
    private String tradeDistributionAlgorithm;
}
