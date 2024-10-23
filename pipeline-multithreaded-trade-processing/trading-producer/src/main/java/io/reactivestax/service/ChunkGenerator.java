package io.reactivestax.service;

import java.io.IOException;

public interface ChunkGenerator {
    void generateChunks() throws IOException, InterruptedException;
}
