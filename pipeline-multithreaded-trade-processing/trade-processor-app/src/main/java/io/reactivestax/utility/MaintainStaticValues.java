package io.reactivestax.utility;

public class MaintainStaticValues {

    private MaintainStaticValues() {
    }

    static long rowsPerFile = 0;
    static int numberOfChunks = 0;
    static String filePath = "";
    static String chunkFilePath = "";
    static int maxRetryCount = 0;
    static String dbName = "";
    static String portNumber = "";
    static String username = "";
    static String password = "";

    public static long getRowsPerFile() {
        return rowsPerFile;
    }

    public static void setRowsPerFile(long rowsPerFile) {
        MaintainStaticValues.rowsPerFile = rowsPerFile;
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

    public static String getChunkFilePath() {
        return chunkFilePath;
    }

    public static void setChunkFilePath(String chunkFilePath) {
        MaintainStaticValues.chunkFilePath = chunkFilePath;
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
}
