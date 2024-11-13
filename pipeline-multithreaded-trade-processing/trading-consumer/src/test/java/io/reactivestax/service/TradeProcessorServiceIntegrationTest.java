package io.reactivestax.service;

import static io.reactivestax.suppliers.dto.DTOSuppliers.GOOD_TRADE_PAYLOAD_TRADE_NUMBER;
import static io.reactivestax.suppliers.dto.DTOSuppliers.goodTradePayloadDTOSupplier;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Optional;

import io.reactivestax.repository.hibernate.entity.JournalEntry;
import io.reactivestax.type.dto.JournalEntryDTO;
import io.reactivestax.type.enums.Direction;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import io.reactivestax.repository.JournalEntryRepository;
import io.reactivestax.repository.LookupSecuritiesRepository;
import io.reactivestax.repository.PositionsRepository;
import io.reactivestax.repository.TradePayloadRepository;
import io.reactivestax.type.dto.TradePayloadDTO;
import io.reactivestax.util.ApplicationPropertiesUtils;
import io.reactivestax.util.database.TransactionUtil;
import io.reactivestax.util.factory.BeanFactory;
import io.reactivestax.util.messaging.rabbitmq.RabbitMQRetry;
import lombok.extern.log4j.Log4j2;

@ExtendWith(MockitoExtension.class)
@Log4j2
class TradeProcessorServiceIntegrationTest {

    @Mock
    private TransactionUtil transactionUtil;
    @Mock
    private TradePayloadRepository tradePayloadRepository;
    @Mock
    private LookupSecuritiesRepository lookupSecuritiesRepositoryMock;
//    @Spy
    private JournalEntryRepository journalEntryRepository;
//    @Mock
    private PositionsRepository positionsRepository;
    @Mock
    private BeanFactory beanFactory;
    @Mock
    private RabbitMQRetry rabbitMQRetry;

    ApplicationPropertiesUtils applicationPropertiesUtils;

    @InjectMocks
    // @Spy
    private TradeProcessorService tradeProcessorServiceSpy;

    @BeforeEach
    public void setUp() throws SQLException {
        MockitoAnnotations.openMocks(this);

        applicationPropertiesUtils = ApplicationPropertiesUtils
                .getInstance("applicationHibernateRabbitMQH2Test.properties");
        //applicationPropertiesUtils.loadApplicationProperties("applicationHibernateRabbitMQH2Test.properties");

        transactionUtil = BeanFactory.getTransactionUtil();

        tradePayloadRepository = BeanFactory.getTradePayloadRepository();
        journalEntryRepository = BeanFactory.getJournalEntryRepository();
        positionsRepository = BeanFactory.getPositionsRepository();

        TradeProcessorService tradeProcessorService = TradeProcessorService.getInstance();
        tradeProcessorServiceSpy = spy(tradeProcessorService);
        tradeProcessorServiceSpy.setLookupSecuritiesRepository(lookupSecuritiesRepositoryMock);
        log.info(() -> "TradeProcessorServiceIntegrationTest setup done");
    }

    @Test
    void testProcessTradePayloadCalledOnce() throws InterruptedException, IOException {
        final String testQueueName = "queue1";

        // Arrange
        // this is to simulate a pre-saved raw tradepayload, that trade processor will
        // pickup and process
        tradePayloadRepository.saveTradePayload(goodTradePayloadDTOSupplier.get());
        //
        String[] payloadArray = goodTradePayloadDTOSupplier.get().getPayload().split(",");
        String testCUSIP = payloadArray[3];
        when(lookupSecuritiesRepositoryMock.lookupSecurities(testCUSIP)).thenReturn(true);

        // Act
        tradeProcessorServiceSpy.processTrade(GOOD_TRADE_PAYLOAD_TRADE_NUMBER, testQueueName);

        // Assert
        Optional<TradePayloadDTO> optionalTradePayload = tradePayloadRepository.readRawPayload(
                GOOD_TRADE_PAYLOAD_TRADE_NUMBER);
        assertEquals(true, optionalTradePayload.isPresent());
        //pending assert for ensuring status update is done as well or not

        assertEquals(goodTradePayloadDTOSupplier.get().getTradeNumber(),
                optionalTradePayload.get().getTradeNumber());
        verify(lookupSecuritiesRepositoryMock, times(1)).lookupSecurities(testCUSIP);
        verify(tradeProcessorServiceSpy, times(1))
                .processTrade(any(), any());


        //journalEntry Assertions
        JournalEntryDTO journalEntryDTO = JournalEntryDTO.builder()
                .tradeId(payloadArray[0])
                .accountNumber(payloadArray[2])
                .securityCusip(payloadArray[3])
                .direction(payloadArray[4])
                .quantity(Integer.parseInt(payloadArray[5]))
                .transactionTimestamp(payloadArray[1])
                .build();

        JournalEntry returnedJournalEntry = journalEntryRepository.findJournalEntryByJournalEntry(journalEntryDTO);

        assertEquals(journalEntryDTO.getTradeId(),returnedJournalEntry.getTradeId());
        assertEquals(journalEntryDTO.getAccountNumber(),returnedJournalEntry.getAccountNumber());
        assertEquals(journalEntryDTO.getSecurityCusip(),returnedJournalEntry.getSecurityCusip());
        assertEquals(Direction.valueOf(journalEntryDTO.getDirection()),returnedJournalEntry.getDirection());
        assertEquals(journalEntryDTO.getQuantity(),returnedJournalEntry.getQuantity());

        //position assertions
        //pending..

    }
}