package io.reactivestax.service;

import io.reactivestax.repository.TradePayloadRepository;
import io.reactivestax.util.ApplicationPropertiesUtils;
import io.reactivestax.util.database.TransactionUtil;
import io.reactivestax.util.factory.BeanFactory;
import io.reactivestax.util.messaging.MessageSender;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mockStatic;

@ExtendWith({MockitoExtension.class})
class ChunkProcessorServiceTest {

    @Mock
    ApplicationPropertiesUtils applicationPropertiesUtilsMock;

    @Mock
    private TransactionUtil transactionUtilMock;

    @Mock
    private MessageSender messageSenderMock;

    @Mock
    private TradePayloadRepository tradePayloadRepositoryMock;

    @InjectMocks
    private ChunkProcessorService chunkProcessorService = ChunkProcessorService.getInstance();

    @Test
    void testProcessChunk() {
        try (MockedStatic<TransactionUtil> transactionUtilMockedStatic = mockStatic(TransactionUtil.class);
             MockedStatic<MessageSender> messageSenderMockedStatic = mockStatic(MessageSender.class);
             MockedStatic<TradePayloadRepository> tradePayloadRepositoryMockedStatic =
                     mockStatic(TradePayloadRepository.class);
             MockedStatic<ApplicationPropertiesUtils> applicationPropertiesUtilsMockedStatic =
                     mockStatic(ApplicationPropertiesUtils.class)) {
            applicationPropertiesUtilsMockedStatic
                    .when(ApplicationPropertiesUtils::getInstance)
                    .thenReturn(applicationPropertiesUtilsMock);
            transactionUtilMockedStatic
                    .when(BeanFactory::getTransactionUtil)
                    .thenReturn(transactionUtilMock);
            messageSenderMockedStatic
                    .when(BeanFactory::getMessageSender)
                    .thenReturn(messageSenderMock);
            tradePayloadRepositoryMockedStatic
                    .when(BeanFactory::getTradePayloadRepository)
                    .thenReturn(tradePayloadRepositoryMock);
            doNothing().when(transactionUtilMock).startTransaction();
            doNothing().when(tradePayloadRepositoryMock).insertTradeRawPayload(any());
            doNothing().when(transactionUtilMock).commitTransaction();
            chunkProcessorService.processChunk("src/test/resources/trades_10000_sameAccountAndPosition.csv");

        }//
    }
}
