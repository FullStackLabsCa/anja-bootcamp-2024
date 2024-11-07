package io.reactivestax.service;

import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.Optional;
import java.util.function.Supplier;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import io.reactivestax.repository.JournalEntryRepository;
import io.reactivestax.repository.LookupSecuritiesRepository;
import io.reactivestax.repository.PositionsRepository;
import io.reactivestax.repository.TradePayloadRepository;
import io.reactivestax.type.dto.TradePayloadDTO;
import io.reactivestax.type.enums.LookupStatus;
import io.reactivestax.type.enums.PostedStatus;
import io.reactivestax.type.enums.ValidityStatus;
import io.reactivestax.util.database.TransactionUtil;

public class TradeProcessorServiceTest {

    @Mock
    private TransactionUtil transactionUtil;
    @Mock
    private TradePayloadRepository tradePayloadRepository;
    @Mock
    private LookupSecuritiesRepository lookupSecuritiesRepository;
    @Mock
    private JournalEntryRepository journalEntryRepository;
    @Mock
    private PositionsRepository positionsRepository;

    @InjectMocks
    private TradeProcessorService tradeProcessorService;

    private Supplier<TradePayloadDTO> tradePayloadSupplier = () -> TradePayloadDTO.builder()
            .tradeNumber("123")
            .payload("TDB_00000000,2024-09-19 22:16:18,TDB_CUST_5214938,V,SELL,683,638.02")
            .lookupStatus(String.valueOf(LookupStatus.PASS))
            .validityStatus(String.valueOf(ValidityStatus.VALID))
            .journalEntryStatus(String.valueOf(PostedStatus.POSTED))
            .build();

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        tradeProcessorService = spy(new TradeProcessorService(transactionUtil, tradePayloadRepository,
                lookupSecuritiesRepository, journalEntryRepository, positionsRepository));

    }

    @Test
    void testProcessTradePayloadCalledOnce() throws InterruptedException, IOException {
        final String testTradeId = "TDB-000-ABC";
        final String testQueueName = "queue1";

        // Arrange
        when(tradePayloadRepository.readRawPayload(testTradeId)).thenReturn(Optional.of(tradePayloadSupplier.get()));

        // Act
        tradeProcessorService.processTrade(testTradeId, testQueueName);

        // Assert
        verify(tradeProcessorService, times(1)).processTradePayload(tradePayloadSupplier.get());
    }

    @Test
    void testProcessTradePayloadCalledOnceValidReferenceLookupSecurityProcessedOnly()
            throws InterruptedException, IOException {
        final String testTradeId = "TDB-000-ABC";
        final String testQueueName = "queue1";

        // Arrange
        when(tradePayloadRepository.readRawPayload(testTradeId)).thenReturn(Optional.of(tradePayloadSupplier.get()));
        when(lookupSecuritiesRepository.lookupSecurities("V")).thenReturn(true);
        // Act
        tradeProcessorService.processTrade(testTradeId, testQueueName);

        // Assert
        verify(tradeProcessorService, times(1)).processTradePayload(tradePayloadSupplier.get());

        verify(tradeProcessorService, times(1)).journalEntryTransaction(Mockito.any(), Mockito.any());

        verify(tradeProcessorService, times(1)).positionTransaction(Mockito.any());

    }

    @Test
    void testJournalEntryTransaction()
            throws InterruptedException, IOException {
        final String testTradeId = "TDB-000-ABC";
        final String testQueueName = "queue1";

        // Arrange
        when(tradePayloadRepository.readRawPayload(testTradeId)).thenReturn(Optional.of(tradePayloadSupplier.get()));
        when(lookupSecuritiesRepository.lookupSecurities("V")).thenReturn(true);
        // Act
        tradeProcessorService.processTrade(testTradeId, testQueueName);

        // Assert
        verify(tradeProcessorService, times(1)).processTradePayload(tradePayloadSupplier.get());

        verify(tradeProcessorService, times(1)).journalEntryTransaction(Mockito.any(), Mockito.any());

        verify(tradeProcessorService, times(1)).positionTransaction(Mockito.any());

    }
}