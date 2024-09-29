package io.reactivestax.service;

import java.io.IOException;

public interface ChunkGenerator {
    void generateChunks(long numOfLines, String path) throws IOException;
}
