package io.reactivestax;

import io.reactivestax.service.ChunkGeneratorService;
import io.reactivestax.service.TradeService;
import io.reactivestax.util.ApplicationPropertiesUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

import static org.junit.Assert.assertEquals;

public class ProducerTest {
    ApplicationPropertiesUtils applicationPropertiesUtils;
    TradeService tradeService;

    @Before
    public void setUp(){
        applicationPropertiesUtils = ApplicationPropertiesUtils.getInstance("applicationTest.properties");
        applicationPropertiesUtils.loadApplicationProperties("applicationTest.properties");
        tradeService = TradeService.getInstance();
    }

    @Test
    public void testGenerateChunks() throws IOException, InterruptedException {
        ApplicationPropertiesUtils.getInstance().setTotalNoOfLines(TradeService.getInstance().fileLineCounter(applicationPropertiesUtils.getFilePath()));
        ChunkGeneratorService chunkGeneratorService = ChunkGeneratorService.getInstance();
        chunkGeneratorService.generateChunks();
        File directory = new File(applicationPropertiesUtils.getChunkDirectoryPath());
        File[] files = directory.listFiles();
        if (files != null) {
            long fileCount = 0;
            for (File file : files) {
                if (file.isFile()) {
                    fileCount++;
                }
            }
            assertEquals(applicationPropertiesUtils.getNumberOfChunks(), fileCount);
        }
    }

    @Test
    public void testFileLineCounter() throws IOException {
        long counter = tradeService.fileLineCounter(applicationPropertiesUtils.getFilePath());
        Assert.assertEquals(9999, counter);
    }

    @Test
    public void testBuildFilePath(){
        String filePath = tradeService.buildFilePath(5, applicationPropertiesUtils.getChunkFilePathWithName());
        Assert.assertEquals(applicationPropertiesUtils.getChunkFilePathWithName()+5+".csv", filePath);
    }
}
