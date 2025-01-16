package io.reactivestax.tradingproducer.task;

import java.io.IOException;

public interface ChunkGenerator {
    void generateChunks() throws IOException, InterruptedException;
}
