package io.reactivestax.service;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TradeServiceTest {
    @Mock
    private ExecutorService executorServiceMock;
    @InjectMocks
    private TradeService tradeServiceMocked;

    @AfterEach
    void tearDown() {
        Mockito.reset(executorServiceMock);
    }

    @Test
    void testStartTradeConsumer() {
        try (MockedStatic<Executors> executorsMockedStatic = mockStatic(Executors.class)) {
            executorsMockedStatic.when(() -> Executors.newFixedThreadPool(anyInt())).thenReturn(executorServiceMock);
            tradeServiceMocked = TradeService.getInstance();
            tradeServiceMocked.startTradeConsumer();
            verify(executorServiceMock, atLeast(2)).submit(any(Callable.class));
        }
    }
}