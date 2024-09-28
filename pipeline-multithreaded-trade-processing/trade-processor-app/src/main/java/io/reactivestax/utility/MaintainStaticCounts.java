package io.reactivestax.utility;

public class MaintainStaticCounts {

    private MaintainStaticCounts() {
    }

    static long rowsPerFile = 0;
    static int numberOfChunks = 0;

    public static void setRowsPerFile(long rows) {
        rowsPerFile = rows;
    }

    public static long getRowsPerFile() {
        return rowsPerFile;
    }

    public static void setNumberOfChunks(int chunks) {
        numberOfChunks = chunks;
    }

    public static int getNumberOfChunks() {
        return numberOfChunks;
    }
}
