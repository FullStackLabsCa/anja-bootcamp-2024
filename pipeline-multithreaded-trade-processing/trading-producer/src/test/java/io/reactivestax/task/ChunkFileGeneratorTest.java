package io.reactivestax.task;

import io.reactivestax.service.ChunkGeneratorService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ChunkFileGeneratorTest {

    @Mock
    private ChunkGeneratorService chunkGeneratorServiceMock;

    @InjectMocks
    private final ChunkFileGenerator chunkFileGenerator = new ChunkFileGenerator();

    @AfterEach
    void tearDown() {
        Mockito.reset(chunkGeneratorServiceMock);
    }

    @Test
    void testRun() throws IOException, InterruptedException {
        try (MockedStatic<ChunkGeneratorService> chunkGeneratorServiceMockedStatic =
                     mockStatic(ChunkGeneratorService.class)) {
            chunkGeneratorServiceMockedStatic.when(ChunkGeneratorService::getInstance).thenReturn(chunkGeneratorServiceMock);
            doNothing().when(chunkGeneratorServiceMock).generateChunks();
            chunkFileGenerator.run();
            verify(chunkGeneratorServiceMock, times(1)).generateChunks();
        }
    }

    @Test
    void testRunWithIOException() throws IOException, InterruptedException {
        try (MockedStatic<ChunkGeneratorService> chunkGeneratorServiceMockedStatic =
                     mockStatic(ChunkGeneratorService.class)) {
            chunkGeneratorServiceMockedStatic.when(ChunkGeneratorService::getInstance).thenReturn(chunkGeneratorServiceMock);
            doThrow(IOException.class).when(chunkGeneratorServiceMock).generateChunks();
            chunkFileGenerator.run();
            verify(chunkGeneratorServiceMock, times(1)).generateChunks();
            assertThrows(IOException.class, () -> chunkGeneratorServiceMock.generateChunks());
        }
    }

    @Test
    void testRunWithInterruptedException() throws IOException, InterruptedException {
        try (MockedStatic<ChunkGeneratorService> chunkGeneratorServiceMockedStatic =
                     mockStatic(ChunkGeneratorService.class)) {
            chunkGeneratorServiceMockedStatic.when(ChunkGeneratorService::getInstance).thenReturn(chunkGeneratorServiceMock);
            doThrow(InterruptedException.class).when(chunkGeneratorServiceMock).generateChunks();
            chunkFileGenerator.run();
            verify(chunkGeneratorServiceMock, times(1)).generateChunks();
            assertThrows(InterruptedException.class, () -> chunkGeneratorServiceMock.generateChunks());
        }
    }
}
