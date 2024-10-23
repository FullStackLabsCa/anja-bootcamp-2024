package io.reactivestax.producer.service;

import java.io.IOException;

public interface ChunkGenerator {
    void generateChunks() throws IOException, InterruptedException;
}
