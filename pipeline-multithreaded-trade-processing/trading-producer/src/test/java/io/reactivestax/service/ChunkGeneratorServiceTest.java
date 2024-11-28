package io.reactivestax.service;

import io.reactivestax.util.ApplicationPropertiesUtils;
import io.reactivestax.util.QueueProvider;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.util.Optional;
import java.util.concurrent.LinkedBlockingQueue;

class ChunkGeneratorServiceTest {
    private ApplicationPropertiesUtils applicationPropertiesUtils;
    private TradeService tradeService;
    private ChunkGeneratorService chunkGeneratorService;

    @BeforeEach
    void setUp() {
        applicationPropertiesUtils = ApplicationPropertiesUtils.getInstance("applicationTest.properties");
        applicationPropertiesUtils.loadApplicationProperties("applicationTest.properties");
        tradeService = TradeService.getInstance();
        chunkGeneratorService = ChunkGeneratorService.getInstance();
    }

    @AfterEach
    void tearDown() {
        File directory = new File(applicationPropertiesUtils.getChunkDirectoryPath());
        boolean delete = directory.delete();
        System.out.println(delete);
    }

    @Test
    void testGenerateChunks() throws IOException, InterruptedException {
        applicationPropertiesUtils.setTotalNoOfLines(tradeService.fileLineCounter(applicationPropertiesUtils.getFilePath()));
        QueueProvider.getInstance().setChunkQueue(new LinkedBlockingQueue<>(applicationPropertiesUtils.getNumberOfChunks()));
        chunkGeneratorService.generateChunks();
        File directory = new File(applicationPropertiesUtils.getChunkDirectoryPath());
        Optional<File[]> files = Optional.ofNullable(directory.listFiles());
        Optional<Integer> optionalCount = files.map(files1 -> files1.length);
        Assertions.assertEquals(Optional.of(applicationPropertiesUtils.getNumberOfChunks()), optionalCount);
    }
}
