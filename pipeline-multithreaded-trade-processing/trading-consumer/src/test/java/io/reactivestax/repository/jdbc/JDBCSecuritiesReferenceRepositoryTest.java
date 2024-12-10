package io.reactivestax.repository.jdbc;

import io.reactivestax.type.exception.QueryFailedException;
import io.reactivestax.util.DbSetUpUtil;
import io.reactivestax.util.database.jdbc.JDBCTransactionUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JDBCSecuritiesReferenceRepositoryTest {
    private JDBCSecuritiesReferenceRepository jdbcSecuritiesReferenceRepository;
    @Mock
    private JDBCTransactionUtil jdbcTransactionUtilMock;
    @Mock
    private Connection connectionMock;
    @Mock
    private PreparedStatement preparedStatementMock;
    @Mock
    private ResultSet resultSetMock;
    @InjectMocks
    private JDBCSecuritiesReferenceRepository jdbcSecuritiesReferenceRepositoryMocked;

    @BeforeEach
    void setUp() throws SQLException {
        jdbcSecuritiesReferenceRepository = JDBCSecuritiesReferenceRepository.getInstance();
        DbSetUpUtil dbSetUpUtil = new DbSetUpUtil();
        dbSetUpUtil.createSecuritiesReferenceTable();
    }

    @Test
    void testLookupSecurities() {
        boolean b = jdbcSecuritiesReferenceRepository.lookupSecurities("AAPL");
        assertTrue(b);
        boolean b1 = jdbcSecuritiesReferenceRepository.lookupSecurities("GOOGL");
        assertTrue(b1);
        boolean b2 = jdbcSecuritiesReferenceRepository.lookupSecurities("V");
        assertFalse(b2);
    }

    @Test
    void testLookupSecuritiesWithException() throws SQLException {
        try (MockedStatic<JDBCTransactionUtil> jdbcTransactionUtilMockedStatic = mockStatic(JDBCTransactionUtil.class)) {
            jdbcTransactionUtilMockedStatic.when(JDBCTransactionUtil::getInstance).thenReturn(jdbcTransactionUtilMock);
            doReturn(connectionMock).when(jdbcTransactionUtilMock).getConnection();
            doReturn(preparedStatementMock).when(connectionMock).prepareStatement(anyString());
            doReturn(resultSetMock).when(preparedStatementMock).executeQuery();
            doThrow(SQLException.class).when(resultSetMock).next();
            assertThrows(QueryFailedException.class, () -> jdbcSecuritiesReferenceRepositoryMocked.lookupSecurities("V"));
        }
    }
}
