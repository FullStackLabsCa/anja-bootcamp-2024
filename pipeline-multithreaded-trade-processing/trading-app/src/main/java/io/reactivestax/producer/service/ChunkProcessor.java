package io.reactivestax.producer.service;

import java.sql.SQLException;

public interface ChunkProcessor {
    void processChunk(String filePath) throws SQLException;
}
