package io.reactivestax.utility;

public class MaintainStaticValues {

    private MaintainStaticValues() {
    }

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

    public static long getTotalNoOfLines() {
        return totalNoOfLines;
    }

    public static void setTotalNoOfLines(long totalNoOfLines) {
        MaintainStaticValues.totalNoOfLines = totalNoOfLines;
    }

    public static int getNumberOfChunks() {
        return numberOfChunks;
    }

    public static void setNumberOfChunks(int numberOfChunks) {
        MaintainStaticValues.numberOfChunks = numberOfChunks;
    }

    public static String getFilePath() {
        return filePath;
    }

    public static void setFilePath(String filePath) {
        MaintainStaticValues.filePath = filePath;
    }

    public static String getChunkDirectoryPath() {
        return chunkDirectoryPath;
    }

    public static void setChunkDirectoryPath(String chunkDirectoryPath) {
        MaintainStaticValues.chunkDirectoryPath = chunkDirectoryPath;
    }

    public static String getChunkFilePathWithName() {
        return chunkFilePathWithName;
    }

    public static void setChunkFilePathWithName(String chunkFilePathWithName) {
        MaintainStaticValues.chunkFilePathWithName = chunkFilePathWithName;
    }

    public static int getMaxRetryCount() {
        return maxRetryCount;
    }

    public static void setMaxRetryCount(int maxRetryCount) {
        MaintainStaticValues.maxRetryCount = maxRetryCount;
    }

    public static String getDbName() {
        return dbName;
    }

    public static void setDbName(String dbName) {
        MaintainStaticValues.dbName = dbName;
    }

    public static String getPortNumber() {
        return portNumber;
    }

    public static void setPortNumber(String portNumber) {
        MaintainStaticValues.portNumber = portNumber;
    }

    public static String getUsername() {
        return username;
    }

    public static void setUsername(String username) {
        MaintainStaticValues.username = username;
    }

    public static String getPassword() {
        return password;
    }

    public static void setPassword(String password) {
        MaintainStaticValues.password = password;
    }

    public static int getChunkProcessorThreadCount() {
        return chunkProcessorThreadCount;
    }

    public static void setChunkProcessorThreadCount(int chunkProcessorThreadCount) {
        MaintainStaticValues.chunkProcessorThreadCount = chunkProcessorThreadCount;
    }

    public static int getTradeProcessorQueueCount() {
        return tradeProcessorQueueCount;
    }

    public static void setTradeProcessorQueueCount(int tradeProcessorQueueCount) {
        MaintainStaticValues.tradeProcessorQueueCount = tradeProcessorQueueCount;
    }

    public static int getTradeProcessorThreadCount() {
        return tradeProcessorThreadCount;
    }

    public static void setTradeProcessorThreadCount(int tradeProcessorThreadCount) {
        MaintainStaticValues.tradeProcessorThreadCount = tradeProcessorThreadCount;
    }

    public static String getTradeDistributionCriteria() {
        return tradeDistributionCriteria;
    }

    public static void setTradeDistributionCriteria(String tradeDistributionCriteria) {
        MaintainStaticValues.tradeDistributionCriteria = tradeDistributionCriteria;
    }
}
