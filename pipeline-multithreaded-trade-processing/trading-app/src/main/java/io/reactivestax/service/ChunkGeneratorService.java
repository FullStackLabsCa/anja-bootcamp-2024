package io.reactivestax.service;

import java.io.IOException;

public interface ChunkGeneratorService {
    void generateChunks() throws IOException, InterruptedException;
}
