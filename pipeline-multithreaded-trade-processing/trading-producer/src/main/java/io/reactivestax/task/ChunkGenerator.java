package io.reactivestax.task;

import java.io.IOException;

public interface ChunkGenerator {
    void generateChunks() throws IOException, InterruptedException;
}
