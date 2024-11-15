package io.reactivestax.service;

import io.reactivestax.util.ApplicationPropertiesUtils;
import io.reactivestax.util.QueueProvider;
import net.bytebuddy.dynamic.scaffold.MethodGraph;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InOrder;
import org.mockito.MockedConstruction;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.concurrent.LinkedBlockingQueue;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class ChunkGeneratorServiceTest {
    public static final String APPLICATION_HIBERNATE_RABBIT_MQH_2_TEST_PROPERTIES = "applicationHibernateRabbitMQH2Test.properties";

    private ApplicationPropertiesUtils applicationPropertiesUtils;

    @BeforeEach
    void setUp() {
        applicationPropertiesUtils = ApplicationPropertiesUtils
                .getInstance(APPLICATION_HIBERNATE_RABBIT_MQH_2_TEST_PROPERTIES);
        applicationPropertiesUtils.loadApplicationProperties(APPLICATION_HIBERNATE_RABBIT_MQH_2_TEST_PROPERTIES);
    }

    @Test
    void testWriteToFile() throws IOException, InterruptedException {
        try (MockedConstruction<FileWriter> mockedFileWriter = Mockito.mockConstruction(FileWriter.class);
             MockedConstruction<BufferedWriter> mockedBufferedWriter = Mockito.mockConstruction(BufferedWriter.class);
             MockedStatic<Files> filesMockedStatic = mockStatic(Files.class);
             MockedStatic<QueueProvider> queueProviderMockedStatic = mockStatic(QueueProvider.class);
             MockedStatic<TradeService> tradeServiceMockedStatic = mockStatic(TradeService.class)) {

            // Mock Files.createDirectories to do nothing, because we do not want to create
            // chunk Dir and create Chunks for REAL. they will get mocked.
            filesMockedStatic.when(() -> Files.createDirectories(any())).thenReturn(null);

            //tradeService mocked to get a tradeChunkFilePath to be verified later
            TradeService tradeServiceMock = mock(TradeService.class);
            tradeServiceMockedStatic.when(TradeService::getInstance).thenReturn(tradeServiceMock);

            String tradeChunkFilePath = "samplePath";
            when(tradeServiceMock.buildNextChunkFilePath(anyInt(), anyString()))
                    .thenReturn("sampleChunkFile1")
                    .thenReturn("sampleChunkFile2")
                    .thenReturn("sampleChunkFile3")
                    .thenReturn("sampleChunkFile4")
                    .thenReturn("sampleChunkFile5")
                    .thenReturn("sampleChunkFile6")
                    .thenReturn("sampleChunkFile7")
                    .thenReturn("sampleChunkFile8")
                    .thenReturn("sampleChunkFile9")
                    .thenReturn("sampleChunkFile10");

            //Example of chained methods getting mocked.
            // Mock QueueProvider.getInstance().getChunkQueue().put() to return a new LinkedBlockingQueue
            QueueProvider mockQueueProvider = mock(QueueProvider.class);
            queueProviderMockedStatic.when(QueueProvider::getInstance).thenReturn(mockQueueProvider);
            // Mock getChunkQueue to return a new LinkedBlockingQueue
            LinkedBlockingQueue<String> mockedLinkedBlockingQueue = mock(LinkedBlockingQueue.class);
            when(mockQueueProvider.getChunkQueue()).thenReturn(mockedLinkedBlockingQueue);
            doNothing().when(mockedLinkedBlockingQueue).put(tradeChunkFilePath);


            // Create an instance of the class under test
            ChunkGeneratorService chunkGeneratorService = ChunkGeneratorService.getInstance();
            // Call the method that uses BufferedWriter and FileWriter
            chunkGeneratorService.generateChunks();

            // Verify that BufferedWriter's write method was called with the expected content
            BufferedWriter mockBufferedWriter0 = mockedBufferedWriter.constructed().get(0);
            verify(mockBufferedWriter0).write("TDB_000001,2024-09-19 22:16:18,TDB_CUST_5214938,TSLA,BUY,1,638.02");
            verify(mockBufferedWriter0).write("TDB_000002,2024-09-19 22:16:18,TDB_CUST_5214938,V,BUY,1,638.02");
            verify(mockBufferedWriter0).write("TDB_000003,2024-09-19 22:16:18,TDB_CUST_5214938,V,BUY,1,638.02");
            verify(mockBufferedWriter0).write("TDB_000004,2024-09-19 22:16:18,TDB_CUST_5214938,TSLABUY,1,638.02");
            verify(mockBufferedWriter0).write("TDB_000005,2024-09-19 22:16:18,TDB_CUST_5214938,TSLA,BUY,1,638.02");
            verify(mockBufferedWriter0).write("TDB_000006,2024-09-19 22:16:18,TDB_CUST_5214938,TSLA,BUY,1,638.02");
            verify(mockBufferedWriter0, times(1000)).write(anyString());
            verify(mockBufferedWriter0).close();

            BufferedWriter mockBufferedWriter1 = mockedBufferedWriter.constructed().get(1);
            verify(mockBufferedWriter1, times(1000)).write(anyString());
            verify(mockBufferedWriter1).close();

            BufferedWriter mockBufferedWriter2 = mockedBufferedWriter.constructed().get(2);
            verify(mockBufferedWriter2, times(1000)).write(anyString());
            verify(mockBufferedWriter2).close();

            BufferedWriter mockBufferedWriter3 = mockedBufferedWriter.constructed().get(3);
            verify(mockBufferedWriter3, times(1000)).write(anyString());
            verify(mockBufferedWriter3).close();

            BufferedWriter mockBufferedWriter4 = mockedBufferedWriter.constructed().get(4);
            verify(mockBufferedWriter4, times(1000)).write(anyString());
            verify(mockBufferedWriter4).close();

            BufferedWriter mockBufferedWriter5 = mockedBufferedWriter.constructed().get(5);
            verify(mockBufferedWriter5, times(1000)).write(anyString());
            verify(mockBufferedWriter5).close();

            BufferedWriter mockBufferedWriter6 = mockedBufferedWriter.constructed().get(6);
            verify(mockBufferedWriter6, times(1000)).write(anyString());
            verify(mockBufferedWriter6).close();

            BufferedWriter mockBufferedWriter7 = mockedBufferedWriter.constructed().get(7);
            verify(mockBufferedWriter7, times(1000)).write(anyString());
            verify(mockBufferedWriter7).close();

            BufferedWriter mockBufferedWriter8 = mockedBufferedWriter.constructed().get(8);
            verify(mockBufferedWriter8, times(1000)).write(anyString());
            verify(mockBufferedWriter8).close();

            BufferedWriter mockBufferedWriter9 = mockedBufferedWriter.constructed().get(9);
            verify(mockBufferedWriter9, times(1005)).write(anyString());
            verify(mockBufferedWriter9).close();


            InOrder inOrder = inOrder(mockedLinkedBlockingQueue);
            inOrder.verify(mockedLinkedBlockingQueue).put("sampleChunkFile1");
            inOrder.verify(mockedLinkedBlockingQueue).put("sampleChunkFile2");
            inOrder.verify(mockedLinkedBlockingQueue).put("sampleChunkFile3");
            inOrder.verify(mockedLinkedBlockingQueue).put("sampleChunkFile4");
            inOrder.verify(mockedLinkedBlockingQueue).put("sampleChunkFile5");
            inOrder.verify(mockedLinkedBlockingQueue).put("sampleChunkFile6");
            inOrder.verify(mockedLinkedBlockingQueue).put("sampleChunkFile7");
            inOrder.verify(mockedLinkedBlockingQueue).put("sampleChunkFile8");
            inOrder.verify(mockedLinkedBlockingQueue).put("sampleChunkFile9");
            inOrder.verify(mockedLinkedBlockingQueue).put("sampleChunkFile10");

            verify(mockedLinkedBlockingQueue, times(10)).put(anyString());
        }
    }
}
