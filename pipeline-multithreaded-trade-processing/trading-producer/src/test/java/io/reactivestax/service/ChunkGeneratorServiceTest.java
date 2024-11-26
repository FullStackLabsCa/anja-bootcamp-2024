package io.reactivestax.service;

import io.reactivestax.util.ApplicationPropertiesUtils;
import io.reactivestax.util.QueueProvider;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.Optional;
import java.util.concurrent.LinkedBlockingQueue;

import static org.junit.Assert.assertEquals;

public class ChunkGeneratorServiceTest {
    ApplicationPropertiesUtils applicationPropertiesUtils;
    TradeService tradeService;
    ChunkGeneratorService chunkGeneratorService;

    @Before
    public void setUp() {
        applicationPropertiesUtils = ApplicationPropertiesUtils.getInstance("applicationTest.properties");
        applicationPropertiesUtils.loadApplicationProperties("applicationTest.properties");
        tradeService = TradeService.getInstance();
        chunkGeneratorService = ChunkGeneratorService.getInstance();
    }

    @After
    public void tearDown() {
        File directory = new File(applicationPropertiesUtils.getChunkDirectoryPath());
        boolean delete = directory.delete();
        System.out.println(delete);
    }

    @Test
    public void testGenerateChunks() throws IOException, InterruptedException {
        applicationPropertiesUtils.setTotalNoOfLines(tradeService.fileLineCounter(applicationPropertiesUtils.getFilePath()));
        QueueProvider.getInstance().setChunkQueue(new LinkedBlockingQueue<>(applicationPropertiesUtils.getNumberOfChunks()));
        chunkGeneratorService.generateChunks();
        File directory = new File(applicationPropertiesUtils.getChunkDirectoryPath());
        Optional<File[]> files = Optional.ofNullable(directory.listFiles());
        Optional<Integer> optionalCount = files.map(files1 -> files1.length);
        assertEquals(Optional.of(applicationPropertiesUtils.getNumberOfChunks()), optionalCount);
    }
}
