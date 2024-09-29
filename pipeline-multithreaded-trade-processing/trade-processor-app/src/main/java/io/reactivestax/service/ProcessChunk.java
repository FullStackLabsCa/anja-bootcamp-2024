package io.reactivestax.service;

import java.sql.SQLException;

public interface ProcessChunk {
    void processChunk() throws SQLException;
}
