package io.reactivestax.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Stream;

import org.hibernate.HibernateException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import io.reactivestax.repository.JournalEntryRepository;
import io.reactivestax.repository.LookupSecuritiesRepository;
import io.reactivestax.repository.PositionsRepository;
import io.reactivestax.repository.TradePayloadRepository;
import io.reactivestax.type.dto.JournalEntryDTO;
import io.reactivestax.type.dto.PositionDTO;
import io.reactivestax.type.dto.TradePayloadDTO;
import io.reactivestax.type.enums.LookupStatus;
import io.reactivestax.type.enums.PostedStatus;
import io.reactivestax.type.enums.ValidityStatus;
import io.reactivestax.type.exception.OptimisticLockingException;
import io.reactivestax.util.database.TransactionUtil;
import io.reactivestax.util.factory.BeanFactory;
import io.reactivestax.util.messaging.rabbitmq.RabbitMQRetry;
import jakarta.persistence.OptimisticLockException;

class TradeProcessorServiceTest {

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
    @Mock
    private BeanFactory beanFactory;
    @Mock
    private RabbitMQRetry rabbitMQRetry;

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
        verify(transactionUtil, times(1)).commitTransaction();
    }

    @ParameterizedTest
    @MethodSource("exceptionProvider")
    void testProcessTradePayloadExceptionCases(Exception exception) throws InterruptedException, IOException {
        final String testTradeId = "TDB-000-ABC";
        final String testQueueName = "queue1";

        // Mock static method
        try (MockedStatic<BeanFactory> mockedBeanFactory = Mockito.mockStatic(BeanFactory.class)) {
            mockedBeanFactory.when(BeanFactory::getTransactionRetryer).thenReturn(rabbitMQRetry);

            // Arrange
            when(tradePayloadRepository.readRawPayload(testTradeId)).thenThrow(exception);
            doNothing().when(rabbitMQRetry).retryTransaction(anyString(), anyString());

            // Act
            tradeProcessorService.processTrade(testTradeId, testQueueName);

            // Assert
            verify(tradeProcessorService, times(0)).processTradePayload(tradePayloadSupplier.get());
            verify(transactionUtil, times(1)).rollbackTransaction();
            verify(rabbitMQRetry, times(1)).retryTransaction(anyString(), anyString());
        }
    }

    private static Stream<Arguments> exceptionProvider() {
        return Stream.of(
                Arguments.of(new HibernateException("Test Hibernate Exception")),
                Arguments.of(new OptimisticLockException("Test Optimistic Lock Exception")),
                Arguments.of(new OptimisticLockingException("Test Optimistic Locking Exception")));
    }

    // @Test
    // void testProcessTradePayloadExceptionCase() throws InterruptedException,
    // IOException {
    // final String testTradeId = "TDB-000-ABC";
    // final String testQueueName = "queue1";

    // // Mock static method
    // try (MockedStatic<BeanFactory> mockedBeanFactory =
    // Mockito.mockStatic(BeanFactory.class)) {
    // mockedBeanFactory.when(BeanFactory::getTransactionRetryer).thenReturn(rabbitMQRetry);

    // // Arrange
    // when(tradePayloadRepository.readRawPayload(testTradeId))
    // .thenThrow(new HibernateException("Test Hibernate Exception"));
    // doNothing().when(rabbitMQRetry).retryTransaction(anyString(), anyString());

    // // Act
    // tradeProcessorService.processTrade(testTradeId, testQueueName);
    // // Assert
    // verify(tradeProcessorService,
    // times(0)).processTradePayload(tradePayloadSupplier.get());
    // verify(transactionUtil, times(1)).rollbackTransaction();
    // verify(rabbitMQRetry, times(1)).retryTransaction(anyString(), anyString());
    // }
    // }

    // @Test
    // void testProcessTradePayloadExceptionCase2() throws InterruptedException,
    // IOException {
    // final String testTradeId = "TDB-000-ABC";
    // final String testQueueName = "queue1";

    // // Mock static method
    // try (MockedStatic<BeanFactory> mockedBeanFactory =
    // Mockito.mockStatic(BeanFactory.class)) {
    // mockedBeanFactory.when(BeanFactory::getTransactionRetryer).thenReturn(rabbitMQRetry);

    // // Arrange -2
    // when(tradePayloadRepository.readRawPayload(testTradeId))
    // .thenThrow(new OptimisticLockException("Test Optimistic Lock Exception"));
    // doNothing().when(rabbitMQRetry).retryTransaction(anyString(), anyString());

    // // Act -2
    // tradeProcessorService.processTrade(testTradeId, testQueueName);
    // // Assert -2
    // verify(tradeProcessorService,
    // times(0)).processTradePayload(tradePayloadSupplier.get());
    // verify(transactionUtil, times(1)).rollbackTransaction();
    // verify(rabbitMQRetry, times(1)).retryTransaction(anyString(), anyString());

    // }
    // }

    // @Test
    // void testProcessTradePayloadExceptionCase3() throws InterruptedException,
    // IOException {
    // final String testTradeId = "TDB-000-ABC";
    // final String testQueueName = "queue1";

    // // Mock static method
    // try (MockedStatic<BeanFactory> mockedBeanFactory =
    // Mockito.mockStatic(BeanFactory.class)) {
    // mockedBeanFactory.when(BeanFactory::getTransactionRetryer).thenReturn(rabbitMQRetry);

    // // Arrange-3
    // when(tradePayloadRepository.readRawPayload(testTradeId))
    // .thenThrow(new OptimisticLockingException("Test Optimistic Locking
    // Exception"));
    // doNothing().when(rabbitMQRetry).retryTransaction(anyString(), anyString());
    // // Act
    // tradeProcessorService.processTrade(testTradeId, testQueueName);
    // // Assert
    // verify(tradeProcessorService,
    // times(0)).processTradePayload(tradePayloadSupplier.get());
    // verify(transactionUtil, times(1)).rollbackTransaction();
    // verify(rabbitMQRetry, times(1)).retryTransaction(anyString(), anyString());

    // }
    // }

    @ParameterizedTest
    @MethodSource("securityReferenceLookupProvider")
    void testProcessTradePayloadBasedOnSecurityReferenceLookup(boolean lookupResult, int journalEntryTimes,
            int positionTransactionTimes) throws InterruptedException, IOException {
        final String testTradeId = "TDB-000-ABC";
        final String testQueueName = "queue1";

        // Arrange
        when(tradePayloadRepository.readRawPayload(testTradeId)).thenReturn(Optional.of(tradePayloadSupplier.get()));
        when(lookupSecuritiesRepository.lookupSecurities("V")).thenReturn(lookupResult);

        // Act
        tradeProcessorService.processTrade(testTradeId, testQueueName);

        // Assert
        verify(tradeProcessorService, times(1)).processTradePayload(tradePayloadSupplier.get());
        verify(tradeProcessorService, times(journalEntryTimes)).executeJournalEntryTransaction(Mockito.any(),
                Mockito.any());
        verify(tradeProcessorService, times(positionTransactionTimes)).executePositionTransaction(Mockito.any());
    }

    private static Stream<Arguments> securityReferenceLookupProvider() {
        return Stream.of(
                Arguments.of(true, 1, 1),
                Arguments.of(false, 0, 0));
    }

    // @Test
    // void testProcessTradePayloadValidSecurityReferenceLookup()
    // throws InterruptedException, IOException {
    // final String testTradeId = "TDB-000-ABC";
    // final String testQueueName = "queue1";

    // // Arrange
    // when(tradePayloadRepository.readRawPayload(testTradeId)).thenReturn(Optional.of(tradePayloadSupplier.get()));
    // when(lookupSecuritiesRepository.lookupSecurities("V")).thenReturn(true);
    // // Act
    // tradeProcessorService.processTrade(testTradeId, testQueueName);

    // // Assert
    // verify(tradeProcessorService,
    // times(1)).processTradePayload(tradePayloadSupplier.get());

    // verify(tradeProcessorService,
    // times(1)).journalEntryTransaction(Mockito.any(), Mockito.any());

    // verify(tradeProcessorService, times(1)).positionTransaction(Mockito.any());

    // }

    // @Test
    // void testProcessTradePayloadInValidSecurityReferenceLookup()
    // throws InterruptedException, IOException {
    // final String testTradeId = "TDB-000-ABC";
    // final String testQueueName = "queue1";

    // // Arrange
    // when(tradePayloadRepository.readRawPayload(testTradeId)).thenReturn(Optional.of(tradePayloadSupplier.get()));
    // when(lookupSecuritiesRepository.lookupSecurities("V")).thenReturn(false);
    // // Act
    // tradeProcessorService.processTrade(testTradeId, testQueueName);

    // // Assert
    // verify(tradeProcessorService,
    // times(1)).processTradePayload(tradePayloadSupplier.get());

    // verify(tradeProcessorService,
    // times(0)).journalEntryTransaction(Mockito.any(), Mockito.any());

    // verify(tradeProcessorService, times(0)).positionTransaction(Mockito.any());

    // }

    @Test
    void testSaveJournalEntry() {
        String[] payloadArr = { "123", "2024-09-19 22:16:18", "TDB_CUST_5214938", "CUSIP123", "SELL", "100" };
        Long tradeId = 1L;

        JournalEntryDTO journalEntryDTO = JournalEntryDTO.builder()
                .tradeId(payloadArr[0])
                .accountNumber(payloadArr[2])
                .securityCusip(payloadArr[3])
                .direction(payloadArr[4])
                .quantity(Integer.parseInt(payloadArr[5]))
                .transactionTimestamp(payloadArr[1])
                .build();

        when(journalEntryRepository.saveJournalEntry(any(JournalEntryDTO.class))).thenReturn(Optional.of(1L));

        JournalEntryDTO journalEntryResultDTO = tradeProcessorService.executeJournalEntryTransaction(payloadArr,
                tradeId);

        verify(journalEntryRepository, times(1)).saveJournalEntry(any(JournalEntryDTO.class));
        verify(tradePayloadRepository, times(1)).updateTradePayloadPostedStatus(tradeId);
        assertEquals(1L, journalEntryResultDTO.getId());
        assertEquals(journalEntryDTO.getTradeId(), journalEntryResultDTO.getTradeId());
        assertEquals(journalEntryDTO.getAccountNumber(), journalEntryResultDTO.getAccountNumber());
        assertEquals(journalEntryDTO.getSecurityCusip(), journalEntryResultDTO.getSecurityCusip());
        assertEquals(journalEntryDTO.getDirection(), journalEntryResultDTO.getDirection());
        assertEquals(journalEntryDTO.getQuantity(), journalEntryResultDTO.getQuantity());
        assertEquals(journalEntryDTO.getTransactionTimestamp(), journalEntryResultDTO.getTransactionTimestamp());
    }

    @Test
    void testPositionTransaction() {
        // Arrange
        JournalEntryDTO journalEntryDTO = JournalEntryDTO.builder()
                .id(1L)
                .accountNumber("TDB_CUST_5214938")
                .securityCusip("CUSIP123")
                .direction("SELL")
                .quantity(100)
                .build();

        PositionDTO expectedPositionDTO = new PositionDTO();
        expectedPositionDTO.setAccountNumber(journalEntryDTO.getAccountNumber());
        expectedPositionDTO.setSecurityCusip(journalEntryDTO.getSecurityCusip());
        expectedPositionDTO.setHolding(-100L);

        // Act
        tradeProcessorService.executePositionTransaction(journalEntryDTO);

        // Assert
        verify(positionsRepository, times(1)).upsertPosition(any(PositionDTO.class));
        verify(journalEntryRepository, times(1)).updateJournalEntryStatus(journalEntryDTO.getId());

        // Additional assertions to verify the Position object
        ArgumentCaptor<PositionDTO> positionCaptor = ArgumentCaptor.forClass(PositionDTO.class);
        verify(positionsRepository).upsertPosition(positionCaptor.capture());
        PositionDTO actualPosition = positionCaptor.getValue();

        assertEquals(expectedPositionDTO.getAccountNumber(), actualPosition.getAccountNumber());
        assertEquals(expectedPositionDTO.getSecurityCusip(), actualPosition.getSecurityCusip());
        assertEquals(expectedPositionDTO.getHolding(), actualPosition.getHolding());
    }
}