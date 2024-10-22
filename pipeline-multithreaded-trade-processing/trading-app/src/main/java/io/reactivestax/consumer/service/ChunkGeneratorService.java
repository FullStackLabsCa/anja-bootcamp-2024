package io.reactivestax.consumer.service;

import java.io.IOException;

public interface ChunkGeneratorService {
    void generateChunks() throws IOException, InterruptedException;
}
