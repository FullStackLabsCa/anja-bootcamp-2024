package io.reactivestax.task;

import io.reactivestax.service.ChunkProcessorService;
import io.reactivestax.util.QueueProvider;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Answers;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.SQLException;
import java.util.concurrent.LinkedBlockingQueue;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ChunkFileProcessorTest {

    @Mock
    private QueueProvider queueProviderMock;

    @Mock
    private ChunkProcessorService chunkProcessorServiceMock;

    @AfterEach
    void tearDown() {
        Mockito.reset(queueProviderMock, chunkProcessorServiceMock);
    }

    @Test
    void testRun() throws InterruptedException, SQLException {
        try (MockedStatic<QueueProvider> queueProviderMockedStatic = mockStatic(QueueProvider.class);
             MockedStatic<ChunkProcessorService> chunkProcessorServiceMockedStatic = mockStatic(ChunkProcessorService.class)) {
            queueProviderMockedStatic.when(QueueProvider::getInstance).thenReturn(queueProviderMock);
            chunkProcessorServiceMockedStatic.when(ChunkProcessorService::getInstance).thenReturn(chunkProcessorServiceMock);
            LinkedBlockingQueue<String> chunkQueue = new LinkedBlockingQueue<>();
            chunkQueue.put("chunk_path");
            when(queueProviderMock.getChunkQueue()).thenReturn(chunkQueue);
            doNothing().when(chunkProcessorServiceMock).processChunk(anyString());
            new ChunkFileProcessor().run();
            verify(queueProviderMock, times(1)).getChunkQueue();
            verify(chunkProcessorServiceMock, times(1)).processChunk(anyString());
        }
    }

    @Test
    void testRunWithInterruptedException() throws InterruptedException {
        try (MockedStatic<QueueProvider> queueProviderMockedStatic = mockStatic(QueueProvider.class)) {
            queueProviderMockedStatic.when(QueueProvider::getInstance).thenReturn(queueProviderMock);
           StringLinkedBlockingQueue chunkQueueMock = mock(StringLinkedBlockingQueue.class);
            when(queueProviderMock.getChunkQueue()).thenReturn(chunkQueueMock);
            when(chunkQueueMock.take()).thenThrow(new InterruptedException());
            new ChunkFileProcessor().run();
            verify(queueProviderMock, times(1)).getChunkQueue();
            assertThrows(InterruptedException.class, () -> queueProviderMock.getChunkQueue().take());
        }
    }

//    @Test
//    void testRunWithSQLException() throws InterruptedException, SQLException{
//        try (MockedStatic<QueueProvider> queueProviderMockedStatic = mockStatic(QueueProvider.class);
//        MockedStatic<ChunkProcessorService> chunkProcessorServiceMockedStatic = mockStatic(ChunkProcessorService.class)) {
//            queueProviderMockedStatic.when(QueueProvider::getInstance).thenReturn(queueProviderMock);
//            chunkProcessorServiceMockedStatic.when(ChunkProcessorService::getInstance).thenReturn(chunkProcessorServiceMock);
//            StringLinkedBlockingQueue chunkQueueMock = mock(StringLinkedBlockingQueue.class);
//            when(queueProviderMock.getChunkQueue()).thenReturn(chunkQueueMock);
//            when(chunkQueueMock.take()).thenReturn("chunk_path");
//            doThrow(SQLException.class).when(chunkProcessorServiceMock).processChunk(anyString());
//            new ChunkFileProcessor().run();
//            assertThrows(SQLException.class, ()->chunkProcessorServiceMock.processChunk(anyString()));
//            Mockito.clearInvocations(chunkQueueMock);
//        }
//    }

    public static class StringLinkedBlockingQueue extends LinkedBlockingQueue<String> {
    }
}
