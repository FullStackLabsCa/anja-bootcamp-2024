package io.reactivestax.service;

import io.reactivestax.util.ApplicationPropertiesUtils;
import io.reactivestax.util.QueueProvider;
import net.bytebuddy.dynamic.scaffold.MethodGraph;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
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

            // Mock Files.createDirectories to do nothing
            filesMockedStatic.when(() -> Files.createDirectories(any())).thenReturn(null);

            TradeService tradeServiceMock = mock(TradeService.class);
            tradeServiceMockedStatic.when(TradeService::getInstance).thenReturn(tradeServiceMock);
            String tradeChunkFilePath = "samplePath";
            when(tradeServiceMock.buildNextChunkFilePath(anyInt(), anyString())).thenReturn(tradeChunkFilePath);

            // Mock QueueProvider.getInstance().getChunkQueue() to return a new LinkedBlockingQueue
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
            BufferedWriter mockBufferedWriter = mockedBufferedWriter.constructed().get(0);
            verify(mockBufferedWriter).write("TDB_000001,2024-09-19 22:16:18,TDB_CUST_5214938,TSLA,BUY,1,638.02");
            verify(mockBufferedWriter).write("TDB_000002,2024-09-19 22:16:18,TDB_CUST_5214938,V,BUY,1,638.02");
            verify(mockBufferedWriter).write("TDB_000003,2024-09-19 22:16:18,TDB_CUST_5214938,V,BUY,1,638.02");
            verify(mockBufferedWriter).write("TDB_000004,2024-09-19 22:16:18,TDB_CUST_5214938,TSLABUY,1,638.02");
            verify(mockBufferedWriter).write("TDB_000005,2024-09-19 22:16:18,TDB_CUST_5214938,TSLA,BUY,1,638.02");
            verify(mockBufferedWriter).write("TDB_000006,2024-09-19 22:16:18,TDB_CUST_5214938,TSLA,BUY,1,638.02");
            verify(mockBufferedWriter, times(1000)).write(anyString());
            verify(mockBufferedWriter).close();

            //verify queue put
            verify(mockedLinkedBlockingQueue, times(10)).put(tradeChunkFilePath);
        }
    }
}
