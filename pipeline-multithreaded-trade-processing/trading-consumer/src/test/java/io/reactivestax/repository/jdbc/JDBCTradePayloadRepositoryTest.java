package io.reactivestax.repository.jdbc;

import io.reactivestax.repository.hibernate.entity.TradePayload;
import io.reactivestax.type.enums.LookupStatus;
import io.reactivestax.type.enums.PostedStatus;
import io.reactivestax.type.exception.QueryFailedException;
import io.reactivestax.util.DbSetUpUtil;
import io.reactivestax.util.EntitySupplier;
import io.reactivestax.util.database.ConnectionUtil;
import io.reactivestax.util.database.jdbc.JDBCTransactionUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JDBCTradePayloadRepositoryTest {
    private ConnectionUtil<Connection> connectionUtil;
    private final Supplier<TradePayload> buyTradePayloadEntity = EntitySupplier.buyTradePayloadEntity;
    private static final String INSERT_TRADE_PAYLOAD = "Insert into trade_payloads (trade_number, validity_status, " + "payload, je_status, lookup_status, created_timestamp, updated_timestamp) values(?, ?, ?, ?, ?, NOW(), " + "NOW())";
    private JDBCTradePayloadRepository jdbcTradePayloadRepository;
    @Mock
    private JDBCTransactionUtil jdbcTransactionUtilMock;
    @Mock
    private Connection connectionMock;
    @InjectMocks
    private JDBCTradePayloadRepository jdbcTradePayloadRepositoryMocked;
    private final DbSetUpUtil dbSetUpUtil = new DbSetUpUtil();

    @BeforeEach
    void setUp() throws SQLException {
        connectionUtil = JDBCTransactionUtil.getInstance();
        jdbcTradePayloadRepository = JDBCTradePayloadRepository.getInstance();
        dbSetUpUtil.createTradePayloadsTable();
    }

    @AfterEach
    void tearDown() {
        Mockito.reset(jdbcTransactionUtilMock, connectionMock);
    }

    @Test
    void testReadRawPayload() throws SQLException {
        TradePayload tradePayloadEntity = buyTradePayloadEntity.get();
        insertIntoTradePayload(tradePayloadEntity);
        io.reactivestax.type.dto.TradePayload tradePayload = jdbcTradePayloadRepository.readRawPayload("TDB_000001");
        assertEquals(tradePayloadEntity.getTradeNumber(), tradePayload.getTradeNumber());
        assertEquals(tradePayloadEntity.getPayload(), tradePayload.getPayload());
        assertEquals(LookupStatus.NOT_CHECKED.name(), tradePayload.getLookupStatus());
        assertEquals(tradePayloadEntity.getJournalEntryStatus().name(), tradePayload.getJournalEntryStatus());
        assertEquals(tradePayloadEntity.getValidityStatus().name(), tradePayload.getValidityStatus());
    }

    @Test
    void testReadRawPayloadWithException() throws SQLException {
        try (MockedStatic<JDBCTransactionUtil> jdbcTransactionUtilMockedStatic = mockStatic(JDBCTransactionUtil.class)) {
            jdbcTransactionUtilMockedStatic.when(JDBCTransactionUtil::getInstance).thenReturn(jdbcTransactionUtilMock);
            doReturn(connectionMock).when(jdbcTransactionUtilMock).getConnection();
            doThrow(SQLException.class).when(connectionMock).prepareStatement(anyString());
            assertThrows(QueryFailedException.class, () -> jdbcTradePayloadRepositoryMocked.readRawPayload("TDB_000001"));
        }
    }

    @Test
    void testUpdateTradePayloadLookupStatus() throws SQLException {
        TradePayload tradePayloadEntity = buyTradePayloadEntity.get();
        insertIntoTradePayload(tradePayloadEntity);
        jdbcTradePayloadRepository.updateTradePayloadLookupStatus(true, 1L);
        io.reactivestax.type.dto.TradePayload tradePayload = jdbcTradePayloadRepository.readRawPayload("TDB_000001");
        assertEquals(LookupStatus.PASS.name(), tradePayload.getLookupStatus());
    }

    @Test
    void testUpdateTradePayloadLookupStatusWithException() throws SQLException {
        try (MockedStatic<JDBCTransactionUtil> jdbcTransactionUtilMockedStatic = mockStatic(JDBCTransactionUtil.class)) {
            jdbcTransactionUtilMockedStatic.when(JDBCTransactionUtil::getInstance).thenReturn(jdbcTransactionUtilMock);
            doReturn(connectionMock).when(jdbcTransactionUtilMock).getConnection();
            doThrow(SQLException.class).when(connectionMock).prepareStatement(anyString());
            assertThrows(QueryFailedException.class, () -> jdbcTradePayloadRepository.updateTradePayloadLookupStatus(true, 1L));
        }
    }

    @Test
    void testUpdateTradePayloadPostedStatus() throws SQLException {
        TradePayload tradePayloadEntity = buyTradePayloadEntity.get();
        insertIntoTradePayload(tradePayloadEntity);
        jdbcTradePayloadRepository.updateTradePayloadPostedStatus(1L);
        io.reactivestax.type.dto.TradePayload tradePayload = jdbcTradePayloadRepository.readRawPayload("TDB_000001");
        assertEquals(PostedStatus.POSTED.name(), tradePayload.getJournalEntryStatus());
    }

    @Test
    void testUpdateTradePayloadPostedStatusWithException() throws SQLException {
        try (MockedStatic<JDBCTransactionUtil> jdbcTransactionUtilMockedStatic = mockStatic(JDBCTransactionUtil.class)) {
            jdbcTransactionUtilMockedStatic.when(JDBCTransactionUtil::getInstance).thenReturn(jdbcTransactionUtilMock);
            doReturn(connectionMock).when(jdbcTransactionUtilMock).getConnection();
            doThrow(SQLException.class).when(connectionMock).prepareStatement(anyString());
            assertThrows(QueryFailedException.class, () -> jdbcTradePayloadRepository.updateTradePayloadPostedStatus(1L));
        }
    }

    private void insertIntoTradePayload(TradePayload tradePayloadEntity) throws SQLException {
        Connection connection = connectionUtil.getConnection();
        try (PreparedStatement preparedStatement = connection.prepareStatement(INSERT_TRADE_PAYLOAD)) {
            preparedStatement.setString(1, tradePayloadEntity.getTradeNumber());
            preparedStatement.setString(2, tradePayloadEntity.getValidityStatus().toString());
            preparedStatement.setString(3, tradePayloadEntity.getPayload());
            preparedStatement.setString(4, tradePayloadEntity.getJournalEntryStatus().toString());
            preparedStatement.setString(5, tradePayloadEntity.getLookupStatus().toString());
            preparedStatement.execute();
        }
    }
}
