package io.reactivestax;

import io.reactivestax.service.TradeService;
import io.reactivestax.util.ApplicationPropertiesUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProducerRunnerTest {

    @Mock
    ApplicationPropertiesUtils applicationPropertiesUtilsMock;

    @Mock
    TradeService tradeServiceMock;

    @InjectMocks
    ProducerRunner producerRunner;

    @AfterEach
    void tearDown() {
        Mockito.reset(applicationPropertiesUtilsMock, tradeServiceMock);
    }

    @Test
    void testStartWithValidTechnology() {
        try (MockedStatic<ApplicationPropertiesUtils> applicationPropertiesUtilsMockedStatic =
                     mockStatic(ApplicationPropertiesUtils.class);
             MockedStatic<TradeService> tradeServiceMockedStatic = mockStatic(TradeService.class)) {
            applicationPropertiesUtilsMockedStatic.when(ApplicationPropertiesUtils::getInstance).thenReturn(applicationPropertiesUtilsMock);
            tradeServiceMockedStatic.when(TradeService::getInstance).thenReturn(tradeServiceMock);
            when(applicationPropertiesUtilsMock.getMessagingTechnology()).thenReturn("rabbitmq");
            doNothing().when(tradeServiceMock).startTradeProducer();
            producerRunner.start();
            verify(applicationPropertiesUtilsMock, times(1)).getMessagingTechnology();
            verify(tradeServiceMock, times(1)).startTradeProducer();
        }
    }

    @Test
    void testStartWithInvalidTechnology() {
        try (MockedStatic<ApplicationPropertiesUtils> applicationPropertiesUtilsMockedStatic =
                     mockStatic(ApplicationPropertiesUtils.class)) {
            applicationPropertiesUtilsMockedStatic.when(ApplicationPropertiesUtils::getInstance).thenReturn(applicationPropertiesUtilsMock);
            when(applicationPropertiesUtilsMock.getMessagingTechnology()).thenReturn("invalid_tech");
            producerRunner.start();
            verify(applicationPropertiesUtilsMock, times(1)).getMessagingTechnology();
            verify(tradeServiceMock, times(0)).startTradeProducer();
        }
    }
}
