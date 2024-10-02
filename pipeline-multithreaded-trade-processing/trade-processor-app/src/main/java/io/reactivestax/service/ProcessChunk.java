package io.reactivestax.service;

import java.sql.SQLException;

public interface ProcessChunk {
    void processChunk(String filePath) throws SQLException;
}
