package io.reactivestax.repository.jdbc;

import io.reactivestax.type.dto.JournalEntry;
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

import java.sql.*;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JDBCJournalEntryRepositoryTest {
    private JDBCJournalEntryRepository jdbcJournalEntryRepository;
    private final DbSetUpUtil dbSetUpUtil = new DbSetUpUtil();
    private ConnectionUtil<Connection> connectionUtil;
    private final Supplier<JournalEntry> journalEntrySupplier = EntitySupplier.journalEntrySupplier;
    private final Logger logger = Logger.getLogger(JDBCJournalEntryRepositoryTest.class.getName());
    @Mock
    private JDBCTransactionUtil jdbcTransactionUtilMock;
    @Mock
    private Connection connectionMock;
    @Mock
    private PreparedStatement preparedStatementMock;
    @Mock
    private ResultSet resultSetMock;
    @InjectMocks
    private JDBCJournalEntryRepository jdbcJournalEntryRepositoryMocked;

    @BeforeEach
    void setUp() throws SQLException {
        dbSetUpUtil.createJournalEntryTable();
        connectionUtil = JDBCTransactionUtil.getInstance();
        jdbcJournalEntryRepository = JDBCJournalEntryRepository.getInstance();
    }

    @AfterEach
    void tearDown(){
        Mockito.reset(jdbcTransactionUtilMock, connectionMock, preparedStatementMock, resultSetMock);
    }

    @Test
    void testInsertIntoJournalEntry() {
        JournalEntry journalEntry = journalEntrySupplier.get();
        Optional<Long> id = jdbcJournalEntryRepository.insertIntoJournalEntry(journalEntry);
        assertTrue(id.isPresent());
        id.ifPresent(i -> assertEquals(1, i));
    }

    @Test
    void testInsertIntoJournalEntryWithInvalidRecord() {
        JournalEntry journalEntry = new JournalEntry();
        journalEntry.setQuantity(10);
        journalEntry.setDirection(null);
        journalEntry.setAccountNumber(null);
        journalEntry.setSecurityCusip(null);
        journalEntry.setTradeId(null);
        assertThrows(QueryFailedException.class, () -> jdbcJournalEntryRepository.insertIntoJournalEntry(journalEntry));
    }

    @Test
    void testInsertIntoJournalEntryForExceptionWhileKeyGeneration() throws SQLException {
        try (MockedStatic<JDBCTransactionUtil> jdbcTransactionUtilMockedStatic = mockStatic(JDBCTransactionUtil.class)) {
            jdbcTransactionUtilMockedStatic.when(JDBCTransactionUtil::getInstance).thenReturn(jdbcTransactionUtilMock);
            doReturn(connectionMock).when(jdbcTransactionUtilMock).getConnection();
            when(connectionMock.prepareStatement(anyString(), eq(Statement.RETURN_GENERATED_KEYS))).thenReturn(preparedStatementMock);
            when(preparedStatementMock.getGeneratedKeys()).thenReturn(resultSetMock);
            doReturn(false).when(resultSetMock).next();
            JournalEntry journalEntry = journalEntrySupplier.get();
            assertThrows(QueryFailedException.class, () -> jdbcJournalEntryRepositoryMocked.insertIntoJournalEntry(journalEntry));
        }
    }

    @Test
    void testUpdateJournalEntryStatus() {
        JournalEntry journalEntry = journalEntrySupplier.get();
        Optional<Long> id = jdbcJournalEntryRepository.insertIntoJournalEntry(journalEntry);
        id.ifPresent(i -> {
            jdbcJournalEntryRepository.updateJournalEntryStatus(i);
            String query = "Select posted_status from journal_entry where id = ?";
            Connection connection = connectionUtil.getConnection();
            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                preparedStatement.setLong(1, i);
                ResultSet resultSet = preparedStatement.executeQuery();
                if (resultSet.next()) {
                    assertEquals(PostedStatus.POSTED.name(), resultSet.getString(1));
                }
            } catch (SQLException e) {
                logger.info("Failed to fetch from journal entry.");
            }
        });
    }

    @Test
    void testUpdateJournalEntryWithInvalidId() {
        assertThrows(QueryFailedException.class, () -> jdbcJournalEntryRepository.updateJournalEntryStatus(2L));
    }
}
