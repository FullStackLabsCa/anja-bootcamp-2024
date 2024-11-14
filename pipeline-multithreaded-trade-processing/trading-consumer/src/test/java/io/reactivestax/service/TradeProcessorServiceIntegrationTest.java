package io.reactivestax.service;

import static io.reactivestax.suppliers.dto.DTOSuppliers.GOOD_TRADE_PAYLOAD_TRADE_NUMBER;
import static io.reactivestax.suppliers.dto.DTOSuppliers.goodTradePayloadDTOSupplier;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Optional;

import io.reactivestax.repository.hibernate.entity.JournalEntry;
import io.reactivestax.repository.hibernate.entity.Position;
import io.reactivestax.type.dto.JournalEntryDTO;
import io.reactivestax.type.dto.PositionDTO;
import io.reactivestax.type.enums.Direction;
import io.reactivestax.util.database.hibernate.HibernateTransactionUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
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

    private TradePayloadRepository tradePayloadRepository;
    private LookupSecuritiesRepository lookupSecuritiesRepository;
    private JournalEntryRepository journalEntryRepository;

    ApplicationPropertiesUtils applicationPropertiesUtils;

    @InjectMocks
    private TradeProcessorService tradeProcessorServiceSpy;

    @BeforeEach
    public void setUp() throws SQLException {
        MockitoAnnotations.openMocks(this);
        applicationPropertiesUtils = ApplicationPropertiesUtils
                .getInstance("applicationHibernateRabbitMQH2Test.properties");
        applicationPropertiesUtils.loadApplicationProperties("applicationHibernateRabbitMQH2Test.properties");
        // these are just needed for test setup work, tradeProcessorService is getting its dependencies from BeanFactory calls in its private constructor
        tradePayloadRepository = BeanFactory.getTradePayloadRepository();
        journalEntryRepository = BeanFactory.getJournalEntryRepository();
        lookupSecuritiesRepository = BeanFactory.getLookupSecuritiesRepository();
        //
        loadSampleSecuritiesIntoReferenceTable();
        //
        TradeProcessorService tradeProcessorService = TradeProcessorService.getInstance();
        tradeProcessorServiceSpy = spy(tradeProcessorService);
        log.info(() -> "TradeProcessorServiceIntegrationTest setup done");
    }

    private void loadSampleSecuritiesIntoReferenceTable() {
        String[] cusipArray = {"AAPL", "GOOGL", "AMZN", "MSFT", "TSLA", "NFLX", "FB", "NVDA", "JPM", "VISA", "MA",
                "BAC", "DIS", "INTC", "CSCO", "ORCL", "WMT", "T", "VZ", "ADBE", "CRM", "PYPL", "PFE", "XOM", "UNH", "V"};
        HibernateTransactionUtil.getInstance().startTransaction();
        for (String cusip : cusipArray) {
            lookupSecuritiesRepository.saveSecurity(cusip);
            System.out.println("Loaded sample security: " + cusip);
            log.info(() -> "Loaded sample security: " + cusip);

        }
        HibernateTransactionUtil.getInstance().commitTransaction();
    }

    @Test
    void testProcessTradePayloadCalledOnce() throws InterruptedException, IOException {
        final String testQueueName = "queue1";

        // Arrange
        // this is to simulate a pre-saved raw tradePayLoad, that trade processor will
        // pick up and process
        tradePayloadRepository.saveTradePayload(goodTradePayloadDTOSupplier.get());
        //
        String[] payloadArray = goodTradePayloadDTOSupplier.get().getPayload().split(",");
        String testCUSIP = payloadArray[3];

        // Act
        tradeProcessorServiceSpy.processTrade(GOOD_TRADE_PAYLOAD_TRADE_NUMBER, testQueueName);

        // Assert
        Optional<TradePayloadDTO> optionalTradePayload = tradePayloadRepository.readRawPayload(GOOD_TRADE_PAYLOAD_TRADE_NUMBER);
        assertTrue(optionalTradePayload.isPresent());
        //pending assert for ensuring status update is done as well or not

        assertEquals(goodTradePayloadDTOSupplier.get().getTradeNumber(),optionalTradePayload.get().getTradeNumber());
        assertTrue(lookupSecuritiesRepository.lookupSecurities(testCUSIP));
        verify(tradeProcessorServiceSpy, times(1)).processTrade(any(), any());


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
        assertNotNull(returnedJournalEntry);
        assertEquals(journalEntryDTO.getTradeId(), returnedJournalEntry.getTradeId());
        assertEquals(journalEntryDTO.getAccountNumber(), returnedJournalEntry.getAccountNumber());
        assertEquals(journalEntryDTO.getSecurityCusip(), returnedJournalEntry.getSecurityCusip());
        assertEquals(Direction.valueOf(journalEntryDTO.getDirection()), returnedJournalEntry.getDirection());
        assertEquals(journalEntryDTO.getQuantity(), returnedJournalEntry.getQuantity());

        //position assertions
        PositionDTO positionDTO = PositionDTO.builder()
                .accountNumber(journalEntryDTO.getAccountNumber())
                .securityCusip(journalEntryDTO.getSecurityCusip())
                .holding((long) journalEntryDTO.getQuantity())
                .build();

        Position returnedPosition = BeanFactory.getPositionsRepository().findPositionByPositionDetails(positionDTO);
        assertNotNull(returnedPosition);
        assertEquals(positionDTO.getAccountNumber(), returnedPosition.getPositionCompositeKey().getAccountNumber());
        assertEquals(positionDTO.getSecurityCusip(), returnedPosition.getPositionCompositeKey().getSecurityCusip());
        assertEquals(positionDTO.getHolding(), (-1)*returnedPosition.getHolding());

    }
}