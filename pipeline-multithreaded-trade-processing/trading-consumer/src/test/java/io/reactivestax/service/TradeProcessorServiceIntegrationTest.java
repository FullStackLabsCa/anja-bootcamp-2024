package io.reactivestax.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.spy;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Optional;

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
import io.reactivestax.suppliers.dto.DTOSuppliers;
import io.reactivestax.type.dto.TradePayloadDTO;
import io.reactivestax.util.ApplicationPropertiesUtils;
import io.reactivestax.util.database.TransactionUtil;
import io.reactivestax.util.factory.BeanFactory;
import io.reactivestax.util.messaging.rabbitmq.RabbitMQRetry;

@ExtendWith(MockitoExtension.class)
class TradeProcessorServiceIntegrationTest {

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

        ApplicationPropertiesUtils applicationPropertiesUtils;

        @InjectMocks
        // @Spy
        private TradeProcessorService tradeProcessorService;

        @BeforeEach
        public void setUp() throws SQLException {
                MockitoAnnotations.openMocks(this);

                applicationPropertiesUtils = ApplicationPropertiesUtils
                                .getInstance("applicationHibernateH2Test.properties");
                applicationPropertiesUtils.loadApplicationProperties("applicationHibernateH2Test.properties");

                tradePayloadRepository = BeanFactory.getTradePayloadRepository();
                transactionUtil = BeanFactory.getTransactionUtil();

                tradeProcessorService = spy(TradeProcessorService.getInstance());
        }

        @Test
        void testProcessTradePayloadCalledOnce() throws InterruptedException, IOException {
                final String testQueueName = "queue1";

                // Arrange
                // this is to simulate a pre-saved raw tradepayload, that trade processor will
                // pickup and process
                tradePayloadRepository.saveTradePayload(DTOSuppliers.goodTradePayloadDTOSupplier.get());
                // when(lookupSecuritiesRepository.lookupSecurities(
                // DTOSuppliers.goodTradePayloadDTOSupplier.get().getPayload().split(",")[3]))
                // .thenReturn(true);
                // when(journalEntryRepository.saveJournalEntry(goodTradePayloadDTOSupplier.get()))
                // .thenReturn(TradePayloadDTO.builder().build());
                // when(positionsRepository.savePosition(goodTradePayloadDTOSupplier.get()))
                // .thenReturn(TradePayloadDTO.builder().build());

                // Act
                tradeProcessorService.processTrade(DTOSuppliers.GOOD_TRADE_PAYLOAD_TRADE_NUMBER, testQueueName);

                // Assert
                Optional<TradePayloadDTO> result = tradePayloadRepository.readRawPayload(
                                DTOSuppliers.GOOD_TRADE_PAYLOAD_TRADE_NUMBER);
                assertEquals(true, result.isPresent());
                assertEquals(DTOSuppliers.goodTradePayloadDTOSupplier.get().getTradeNumber(),
                                result.get().getTradeNumber());
        }
}