package io.reactivestax.task;

import java.sql.SQLException;

public interface ChunkProcessor {
    void processChunk(String filePath) throws SQLException;
}
