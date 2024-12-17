package io.reactivestax.service;

import io.reactivestax.repository.JournalEntryRepository;
import io.reactivestax.repository.LookupSecuritiesRepository;
import io.reactivestax.repository.PositionsRepository;
import io.reactivestax.repository.TradePayloadRepository;
import io.reactivestax.repository.hibernate.entity.TradePayload;
import io.reactivestax.type.dto.JournalEntry;
import io.reactivestax.type.dto.Position;
import io.reactivestax.util.EntitySupplier;
import io.reactivestax.util.database.TransactionUtil;
import io.reactivestax.util.factory.BeanFactory;
import io.reactivestax.util.messaging.TransactionRetryer;
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
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TradeProcessorServiceTest {
    @Mock
    private TransactionUtil transactionUtilMock;
    @Mock
    private TradePayloadRepository tradePayloadRepositoryMock;
    @Mock
    private LookupSecuritiesRepository lookupSecuritiesRepositoryMock;
    @Mock
    private JournalEntryRepository journalEntryRepositoryMock;
    @Mock
    private PositionsRepository positionsRepositoryMock;
    @Mock
    private TransactionRetryer transactionRetryerMock;

    @InjectMocks
    private TradeProcessorService tradeProcessorService;

    @AfterEach
    void tearDown() {
        Mockito.reset(transactionUtilMock, tradePayloadRepositoryMock, lookupSecuritiesRepositoryMock,
                journalEntryRepositoryMock, positionsRepositoryMock, transactionRetryerMock);
    }

    @Test
    void testProcessTrade() throws IOException, InterruptedException {
        TradePayload tradePayloadEntity = EntitySupplier.buyTradePayloadEntity.get();
        io.reactivestax.type.dto.TradePayload tradePayload = new io.reactivestax.type.dto.TradePayload(
                1L,
                tradePayloadEntity.getTradeNumber(),
                tradePayloadEntity.getPayload(),
                tradePayloadEntity.getValidityStatus().name(),
                tradePayloadEntity.getLookupStatus().name(),
                tradePayloadEntity.getJournalEntryStatus().name()
        );
        try (MockedStatic<BeanFactory> beanFactoryMockedStatic = mockStatic(BeanFactory.class)) {
            beanFactoryMockedStatic.when(BeanFactory::getTransactionUtil).thenReturn(transactionUtilMock);
            doNothing().when(transactionUtilMock).startTransaction();
            beanFactoryMockedStatic.when(BeanFactory::getTradePayloadRepository).thenReturn(tradePayloadRepositoryMock);
            doReturn(tradePayload).when(tradePayloadRepositoryMock).readRawPayload(anyString());
            beanFactoryMockedStatic.when(BeanFactory::getLookupSecuritiesRepository).thenReturn(lookupSecuritiesRepositoryMock);
            doReturn(true).when(lookupSecuritiesRepositoryMock).lookupSecurities(anyString());
            doNothing().when(tradePayloadRepositoryMock).updateTradePayloadLookupStatus(anyBoolean(), anyLong());
            beanFactoryMockedStatic.when(BeanFactory::getJournalEntryRepository).thenReturn(journalEntryRepositoryMock);
            doReturn(Optional.of(1L)).when(journalEntryRepositoryMock).insertIntoJournalEntry(any(JournalEntry.class));
            doNothing().when(tradePayloadRepositoryMock).updateTradePayloadPostedStatus(anyLong());
            beanFactoryMockedStatic.when(BeanFactory::getPositionsRepository).thenReturn(positionsRepositoryMock);
            doNothing().when(positionsRepositoryMock).upsertPosition(any(Position.class));
            doNothing().when(journalEntryRepositoryMock).updateJournalEntryStatus(anyLong());
            doNothing().when(transactionUtilMock).commitTransaction();
            tradeProcessorService = TradeProcessorService.getInstance();
            tradeProcessorService.processTrade("TDB_000001", "queue");
            verify(transactionUtilMock, atMostOnce()).startTransaction();
            verify(tradePayloadRepositoryMock, atMostOnce()).readRawPayload(anyString());
            verify(lookupSecuritiesRepositoryMock, atMostOnce()).lookupSecurities(anyString());
            verify(tradePayloadRepositoryMock, atMostOnce()).updateTradePayloadLookupStatus(anyBoolean(), anyLong());
            verify(journalEntryRepositoryMock, atMostOnce()).insertIntoJournalEntry(any(JournalEntry.class));
            verify(tradePayloadRepositoryMock, atMostOnce()).updateTradePayloadPostedStatus(anyLong());
            verify(positionsRepositoryMock, atMostOnce()).upsertPosition(any(Position.class));
            verify(journalEntryRepositoryMock, atMostOnce()).updateJournalEntryStatus(anyLong());
            verify(transactionUtilMock, atMostOnce()).commitTransaction();
            beanFactoryMockedStatic.when(BeanFactory::getTransactionRetryer).thenReturn(transactionRetryerMock);
            doThrow(HibernateException.class).when(transactionUtilMock).commitTransaction();
            doNothing().when(transactionRetryerMock).retryTransaction(anyString(), anyString());
            tradeProcessorService.processTrade("TDB_000001", "queue");
            assertThrows(HibernateException.class, transactionUtilMock::commitTransaction);
        }
    }

}
