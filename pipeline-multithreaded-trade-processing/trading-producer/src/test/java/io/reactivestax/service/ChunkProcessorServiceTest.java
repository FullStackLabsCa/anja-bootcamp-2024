package io.reactivestax.service;

import io.reactivestax.repository.TradePayloadRepository;
import io.reactivestax.type.dto.TradePayload;
import io.reactivestax.util.ApplicationPropertiesUtils;
import io.reactivestax.util.database.TransactionUtil;
import io.reactivestax.util.factory.BeanFactory;
import io.reactivestax.util.messaging.MessageSender;
import org.hibernate.HibernateException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith({MockitoExtension.class})
class ChunkProcessorServiceTest {

    @Mock
    private ApplicationPropertiesUtils applicationPropertiesUtilsMock;

    @Mock
    private TransactionUtil transactionUtilMock;

    @Mock
    private MessageSender messageSenderMock;

    @Mock
    private TradePayloadRepository tradePayloadRepositoryMock;

    @InjectMocks
    private ChunkProcessorService chunkProcessorService = ChunkProcessorService.getInstance();

    @AfterEach
    void tearDown() {
        Mockito.reset(applicationPropertiesUtilsMock, transactionUtilMock, messageSenderMock,
                tradePayloadRepositoryMock);
    }

    @Test
    void testProcessChunkWithTradeDistAccNumber() {
        try (
                MockedStatic<ApplicationPropertiesUtils> applicationPropertiesUtilsMockedStatic =
                        mockStatic(ApplicationPropertiesUtils.class);
                MockedStatic<BeanFactory> beanFactoryMockedStatic = mockStatic(BeanFactory.class)) {
            applicationPropertiesUtilsMockedStatic
                    .when(ApplicationPropertiesUtils::getInstance)
                    .thenReturn(applicationPropertiesUtilsMock);
            beanFactoryMockedStatic.when(BeanFactory::getTransactionUtil)
                    .thenReturn(transactionUtilMock);
            beanFactoryMockedStatic.when(BeanFactory::getMessageSender)
                    .thenReturn(messageSenderMock);
            beanFactoryMockedStatic.when(BeanFactory::getTradePayloadRepository)
                    .thenReturn(tradePayloadRepositoryMock);

            doNothing().when(transactionUtilMock).startTransaction();
            doNothing().when(tradePayloadRepositoryMock).insertTradeRawPayload(any(TradePayload.class));
            doNothing().when(transactionUtilMock).commitTransaction();
            when(applicationPropertiesUtilsMock.getQueueExchangeName()).thenReturn("exchange");
            when(applicationPropertiesUtilsMock.getTradeDistributionCriteria()).thenReturn("accountNumber");
            when(applicationPropertiesUtilsMock.getTradeDistributionAlgorithm()).thenReturn("round-robin");
            when(applicationPropertiesUtilsMock.isTradeDistributionUseMap()).thenReturn(false);
            when(applicationPropertiesUtilsMock.getTradeProcessorQueueCount()).thenReturn(3);
            doNothing().when(messageSenderMock).sendMessage(anyString(), anyString());
            chunkProcessorService.processChunk("src/test/resources/trades_10000_sameAccountAndPosition.csv");
            verify(transactionUtilMock, times(10000)).startTransaction();
            verify(transactionUtilMock, times(10000)).commitTransaction();
            verify(applicationPropertiesUtilsMock, times(9999)).getQueueExchangeName();
            verify(applicationPropertiesUtilsMock, times(9999)).getTradeDistributionCriteria();
            verify(applicationPropertiesUtilsMock, times(9999)).getTradeDistributionAlgorithm();
            verify(applicationPropertiesUtilsMock, times(9999)).isTradeDistributionUseMap();
            verify(applicationPropertiesUtilsMock, times(9999)).getTradeProcessorQueueCount();
            verify(messageSenderMock, times(9999)).sendMessage(anyString(), anyString());
        }
    }

    @Test
    void testProcessChunkWithTradeDistTradeId() {
        try (
                MockedStatic<ApplicationPropertiesUtils> applicationPropertiesUtilsMockedStatic =
                        mockStatic(ApplicationPropertiesUtils.class);
                MockedStatic<BeanFactory> beanFactoryMockedStatic = mockStatic(BeanFactory.class)) {
            applicationPropertiesUtilsMockedStatic
                    .when(ApplicationPropertiesUtils::getInstance)
                    .thenReturn(applicationPropertiesUtilsMock);
            beanFactoryMockedStatic.when(BeanFactory::getTransactionUtil)
                    .thenReturn(transactionUtilMock);
            beanFactoryMockedStatic.when(BeanFactory::getMessageSender)
                    .thenReturn(messageSenderMock);
            beanFactoryMockedStatic.when(BeanFactory::getTradePayloadRepository)
                    .thenReturn(tradePayloadRepositoryMock);

            doNothing().when(transactionUtilMock).startTransaction();
            doNothing().when(tradePayloadRepositoryMock).insertTradeRawPayload(any(TradePayload.class));
            doNothing().when(transactionUtilMock).commitTransaction();
            when(applicationPropertiesUtilsMock.getQueueExchangeName()).thenReturn("exchange");
            when(applicationPropertiesUtilsMock.getTradeDistributionCriteria()).thenReturn("tradeId");
            when(applicationPropertiesUtilsMock.getTradeDistributionAlgorithm()).thenReturn("round-robin");
            when(applicationPropertiesUtilsMock.isTradeDistributionUseMap()).thenReturn(false);
            when(applicationPropertiesUtilsMock.getTradeProcessorQueueCount()).thenReturn(3);
            doNothing().when(messageSenderMock).sendMessage(anyString(), anyString());
            chunkProcessorService.processChunk("src/test/resources/trades_10000_sameAccountAndPosition.csv");
            verify(transactionUtilMock, times(10000)).startTransaction();
            verify(transactionUtilMock, times(10000)).commitTransaction();
            verify(applicationPropertiesUtilsMock, times(9999)).getQueueExchangeName();
            verify(applicationPropertiesUtilsMock, times(9999)).getTradeDistributionCriteria();
            verify(applicationPropertiesUtilsMock, times(9999)).getTradeDistributionAlgorithm();
            verify(applicationPropertiesUtilsMock, times(9999)).isTradeDistributionUseMap();
            verify(applicationPropertiesUtilsMock, times(9999)).getTradeProcessorQueueCount();
            verify(messageSenderMock, times(9999)).sendMessage(anyString(), anyString());
        }
    }

    @Test
    void testProcessChunkWithIOException() {
        try (MockedStatic<BeanFactory> beanFactoryMockedStatic = mockStatic(BeanFactory.class);
             MockedStatic<Files> filesMockedStatic = mockStatic(Files.class)) {
            beanFactoryMockedStatic.when(BeanFactory::getTransactionUtil)
                    .thenReturn(transactionUtilMock);
            beanFactoryMockedStatic.when(BeanFactory::getMessageSender)
                    .thenReturn(messageSenderMock);
            beanFactoryMockedStatic.when(BeanFactory::getTradePayloadRepository)
                    .thenReturn(tradePayloadRepositoryMock);
            filesMockedStatic.when(() -> Files.lines(any(Path.class), any(Charset.class))).thenThrow(IOException.class);

            chunkProcessorService.processChunk("src/test/resources/trades_10000_sameAccountAndPosition.csv");
            filesMockedStatic.verify(() -> Files.lines(any(Path.class), any(Charset.class)), times(1));
        }
    }
}
