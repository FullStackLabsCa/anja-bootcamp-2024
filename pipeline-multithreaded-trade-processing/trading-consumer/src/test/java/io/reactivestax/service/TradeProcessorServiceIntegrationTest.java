package io.reactivestax.service;

import io.reactivestax.repository.JournalEntryRepository;
import io.reactivestax.repository.LookupSecuritiesRepository;
import io.reactivestax.repository.TradePayloadRepository;
import io.reactivestax.repository.hibernate.entity.JournalEntry;
import io.reactivestax.repository.hibernate.entity.Position;
import io.reactivestax.repository.hibernate.entity.TradePayload;
import io.reactivestax.suppliers.dto.DTOSuppliers;
import io.reactivestax.type.dto.JournalEntryDTO;
import io.reactivestax.type.dto.PositionDTO;
import io.reactivestax.type.dto.TradePayloadDTO;
import io.reactivestax.type.enums.Direction;
import io.reactivestax.util.ApplicationPropertiesUtils;
import io.reactivestax.util.database.hibernate.HibernateTransactionUtil;
import io.reactivestax.util.factory.BeanFactory;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaDelete;
import lombok.extern.log4j.Log4j2;
import org.hibernate.Session;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

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

    @AfterEach
    public void tearDown() {
        HibernateTransactionUtil.getInstance().startTransaction();
        Session session = HibernateTransactionUtil.getInstance().getConnection();
        CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
        CriteriaDelete<TradePayload> criteriaDeleteTradePayload = criteriaBuilder
                .createCriteriaDelete(io.reactivestax.repository.hibernate.entity.TradePayload.class);
        session.createMutationQuery(criteriaDeleteTradePayload).executeUpdate();
        CriteriaDelete<io.reactivestax.repository.hibernate.entity.JournalEntry> criteriaDeleteJournalEntry = criteriaBuilder
                .createCriteriaDelete(io.reactivestax.repository.hibernate.entity.JournalEntry.class);
        session.createMutationQuery(criteriaDeleteJournalEntry).executeUpdate();
        CriteriaDelete<io.reactivestax.repository.hibernate.entity.Position> criteriaDeletePositions = criteriaBuilder
                .createCriteriaDelete(io.reactivestax.repository.hibernate.entity.Position.class);
        session.createMutationQuery(criteriaDeletePositions).executeUpdate();
        CriteriaDelete<io.reactivestax.repository.hibernate.entity.SecuritiesReference> criteriaDeleteSecuritiesReference = criteriaBuilder
                .createCriteriaDelete(io.reactivestax.repository.hibernate.entity.SecuritiesReference.class);
        session.createMutationQuery(criteriaDeleteSecuritiesReference).executeUpdate();
        HibernateTransactionUtil.getInstance().commitTransaction();
        log.info(() -> "TradeProcessorServiceIntegrationTest teardown done");
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


    @ParameterizedTest
    @MethodSource("payloadProvider")
    void testProcessTradePayloadForSellAndBuyTrade(Supplier<TradePayloadDTO> tradePayloadSupplier) throws InterruptedException, IOException {
        final String testQueueName = "queue1";

        // Arrange
        // this is to simulate a pre-saved raw tradePayLoad, that trade processor will
        // pick up and process
        tradePayloadRepository.saveTradePayload(tradePayloadSupplier.get());
        //
        String[] payloadArray = tradePayloadSupplier.get().getPayload().split(",");
        PayloadRecord payloadRecord = preparePayloadRecordFromArray(payloadArray);
        String securityCUSIP = payloadRecord.securityCusip();

        // Act
        tradeProcessorServiceSpy.processTrade(payloadRecord.tradeId(), testQueueName);

        // Assert
        Optional<TradePayloadDTO> optionalTradePayload = tradePayloadRepository.readRawPayload(payloadRecord.tradeId());
        assertTrue(optionalTradePayload.isPresent());
        //pending assert for ensuring status update is done as well or not

        assertEquals(tradePayloadSupplier.get().getTradeNumber(), optionalTradePayload.get().getTradeNumber());
        assertTrue(lookupSecuritiesRepository.lookupSecurities(securityCUSIP));
        verify(tradeProcessorServiceSpy, times(1)).processTrade(any(), any());


        //journalEntry Assertions
        JournalEntryDTO journalEntryDTO = JournalEntryDTO.builder()
                .tradeId(payloadRecord.tradeId())
                .accountNumber(payloadRecord.accountNumber())
                .securityCusip(payloadRecord.securityCusip())
                .direction(payloadRecord.direction())
                .quantity(payloadRecord.quantity())
                .transactionTimestamp(payloadRecord.transactionTimestamp())
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
        assertEquals(positionDTO.getHolding(), Direction.SELL.name().equals(payloadRecord.direction()) ? (-1) * returnedPosition.getHolding() : returnedPosition.getHolding());

    }

    private static Stream<Arguments> payloadProvider() {
        return Stream.of(
                Arguments.of(DTOSuppliers.sellTradePayloadDTOSupplier),
                Arguments.of(DTOSuppliers.buyTradePayloadDTOSupplier));
    }

    public record PayloadRecord(
            String tradeId,
            String transactionTimestamp,
            String accountNumber,
            String securityCusip,
            String direction,
            int quantity
    ) {}

    PayloadRecord preparePayloadRecordFromArray(String[] payloadArray) {
        return new PayloadRecord(
            payloadArray[0],
            payloadArray[1],
            payloadArray[2],
            payloadArray[3],
            payloadArray[4],
            Integer.parseInt(payloadArray[5])
        );
    }
}