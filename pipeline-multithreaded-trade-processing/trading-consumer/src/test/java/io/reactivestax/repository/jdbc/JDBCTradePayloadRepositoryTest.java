package io.reactivestax.repository.jdbc;

import io.reactivestax.repository.hibernate.entity.TradePayload;
import io.reactivestax.type.enums.LookupStatus;
import io.reactivestax.type.enums.PostedStatus;
import io.reactivestax.type.exception.QueryFailedException;
import io.reactivestax.util.ApplicationPropertiesUtils;
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
import java.sql.Statement;
import java.util.function.Supplier;
import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JDBCTradePayloadRepositoryTest {
    private final Logger logger = Logger.getLogger(JDBCTradePayloadRepositoryTest.class.getName());
    private ConnectionUtil<Connection> connectionUtil;
    private final Supplier<TradePayload> buyTradePayloadEntity = EntitySupplier.buyTradePayloadEntity;
    private static final String INSERT_TRADE_PAYLOAD = "Insert into trade_payloads (trade_number, validity_status, " + "payload, je_status, lookup_status, created_timestamp, updated_timestamp) values(?, ?, ?, ?, ?, NOW(), " + "NOW())";
    private JDBCTradePayloadRepository jdbcTradePayloadRepository;
    @Mock
    JDBCTransactionUtil jdbcTransactionUtilMock;
    @Mock
    Connection connectionMock;
    @InjectMocks
    private JDBCTradePayloadRepository jdbcTradePayloadRepositoryMocked;

    @BeforeEach
    void setUp() throws SQLException {
        ApplicationPropertiesUtils applicationPropertiesUtils = ApplicationPropertiesUtils.getInstance("applicationJDBCTest.properties");
        applicationPropertiesUtils.loadApplicationProperties("applicationJDBCTest.properties");
        connectionUtil = JDBCTransactionUtil.getInstance();
        jdbcTradePayloadRepository = JDBCTradePayloadRepository.getInstance();
        String[] sqlCommands = new String[]{"DROP TABLE IF EXISTS journal_entry", """
                CREATE TABLE trade_payloads (
                    created_timestamp timestamp NOT NULL,
                    id bigint NOT NULL AUTO_INCREMENT,
                    updated_timestamp timestamp NOT NULL,
                    payload varchar(255) NOT NULL,
                    trade_number varchar(255) NOT NULL,
                    je_status varchar(10) NOT NULL CHECK (je_status IN ('POSTED', 'NOT_POSTED')),  -- Adjusted to VARCHAR with CHECK
                    lookup_status varchar(12) NOT NULL CHECK (lookup_status IN ('PASS', 'FAIL', 'NOT_CHECKED')),
                    validity_status varchar(7) NOT NULL CHECK (validity_status IN ('VALID', 'INVALID')),
                    PRIMARY KEY (id),
                    UNIQUE (trade_number)
                )
                """};
        Connection connection = connectionUtil.getConnection();
        try (Statement statement = connection.createStatement()) {
            for (String sql : sqlCommands) {
                statement.execute(sql);
                logger.info("Database setup completed successfully.");
            }
        }
    }

    @AfterEach
    void tearDown() throws SQLException {
        Mockito.reset(jdbcTransactionUtilMock, connectionMock);
        Connection connection = connectionUtil.getConnection();
        try (PreparedStatement preparedStatement = connection.prepareStatement("DROP all objects")) {
            preparedStatement.execute();
            logger.info("All tables dropped successfully.");
        }
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
            TradePayload tradePayloadEntity = buyTradePayloadEntity.get();
            insertIntoTradePayload(tradePayloadEntity);
            doThrow(SQLException.class).when(connectionMock).prepareStatement(anyString());
            assertThrows(QueryFailedException.class, () -> jdbcTradePayloadRepositoryMocked.readRawPayload(null));
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
            TradePayload tradePayloadEntity = buyTradePayloadEntity.get();
            insertIntoTradePayload(tradePayloadEntity);
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
            TradePayload tradePayloadEntity = buyTradePayloadEntity.get();
            insertIntoTradePayload(tradePayloadEntity);
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
