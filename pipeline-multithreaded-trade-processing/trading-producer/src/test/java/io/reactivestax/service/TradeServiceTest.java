package io.reactivestax.service;

import io.reactivestax.util.ApplicationPropertiesUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TradeServiceTest {

    @Mock
    private ExecutorService executorServiceMock;

    @Spy
    @InjectMocks
    private TradeService tradeService = TradeService.getInstance();

    @BeforeEach
    void setUp() {
        ApplicationPropertiesUtils applicationPropertiesUtils = ApplicationPropertiesUtils.getInstance(
                "applicationTest.properties");
        applicationPropertiesUtils.loadApplicationProperties("applicationTest.properties");
    }

    @AfterEach
    void tearDown() {
        Mockito.reset(executorServiceMock, tradeService);
    }

    @Test
    void testFileLineCounter() throws IOException {
        long counter = tradeService.fileLineCounter("src/test/resources/trades_10000_sameAccountAndPosition.csv");
        assertEquals(9999, counter);
    }

    @Test
    void testBuildFilePath() {
        String chunkFilePath = "src/test/resources/chunks/trade_records_chunk";
        String filePath = tradeService.buildFilePath(5, chunkFilePath);
        assertEquals(chunkFilePath + 5 + ".csv", filePath);
    }

    @Test
    void testStartTradeProducer() {
        try (MockedStatic<Executors> executorsMockedStatic = mockStatic(Executors.class)) {
            executorsMockedStatic.when(Executors::newSingleThreadExecutor).thenReturn(executorServiceMock);
            executorsMockedStatic.when(() -> Executors.newFixedThreadPool(anyInt())).thenReturn(executorServiceMock);
            tradeService.startTradeProducer();
            verify(executorServiceMock, times(11)).submit(any(Runnable.class));
        }
    }

    @Test
    void testStartTradeProducerWithException() throws IOException {
        doThrow(IOException.class).when(tradeService).fileLineCounter(anyString());
        tradeService.startTradeProducer();
        verify(tradeService, times(1)).fileLineCounter(anyString());
        assertThrows(IOException.class, ()-> tradeService.fileLineCounter(anyString()));
    }
}
