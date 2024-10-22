package io.reactivestax.consumer.service;

import java.sql.SQLException;

public interface ChunkProcessorService {
    void processChunk(String filePath) throws SQLException;
}
