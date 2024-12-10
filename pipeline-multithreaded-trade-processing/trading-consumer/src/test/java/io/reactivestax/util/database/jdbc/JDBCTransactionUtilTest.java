package io.reactivestax.util.database.jdbc;

import com.zaxxer.hikari.HikariDataSource;
import io.reactivestax.type.exception.SystemInitializationException;
import io.reactivestax.type.exception.TransactionHandlingException;
import io.reactivestax.util.ApplicationPropertiesUtils;
import io.reactivestax.util.database.jdbc.JDBCTransactionUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JDBCTransactionUtilTest {

    private final JDBCTransactionUtil jdbcTransactionUtil = JDBCTransactionUtil.getInstance();

    @Spy
    private HikariDataSource hikariDataSourceSpy;

    @Mock
    private Connection connectionMock;

    @Mock
    private ThreadLocal<Connection> connectionThreadLocalMock;

    @Spy
    @InjectMocks
    private JDBCTransactionUtil jdbcTransactionUtilInjectedMock;

    @BeforeEach
    void setUp() {
        ApplicationPropertiesUtils applicationPropertiesUtils = ApplicationPropertiesUtils.getInstance(
                "applicationTest.properties");
        applicationPropertiesUtils.loadApplicationProperties("applicationTest.properties");
    }

    @AfterEach
    void tearDown() {
        Mockito.reset(hikariDataSourceSpy, connectionMock, connectionThreadLocalMock, jdbcTransactionUtilInjectedMock);
    }

    @Test
    void testGetConnectionSingleThreaded() throws SQLException {
        Connection connection = jdbcTransactionUtil.getConnection();
        assertNotNull(connection);
        assertTrue(connection.getAutoCommit());
    }

    @Test
    void testGetConnectionMultiThreaded() throws ExecutionException, InterruptedException {
        ExecutorService executorService = Executors.newFixedThreadPool(2);
        Callable<Connection> connectionCallable = jdbcTransactionUtil::getConnection;
        Connection connection1 = executorService.submit(connectionCallable).get();
        Connection connection2 = executorService.submit(connectionCallable).get();
        assertNotEquals(connection1, connection2);
        assertNotEquals(connection1.hashCode(), connection2.hashCode());
    }

    @Test
    void testGetConnectionWithException() throws SQLException {
        doThrow(SQLException.class).when(hikariDataSourceSpy).getConnection();
        assertThrows(SQLException.class, hikariDataSourceSpy::getConnection);
        assertThrows(SystemInitializationException.class, jdbcTransactionUtilInjectedMock::getConnection);
    }

    @Test
    void testStartTransaction() throws SQLException {
        jdbcTransactionUtil.startTransaction();
        Connection connection = jdbcTransactionUtil.getConnection();
        assertFalse(connection.getAutoCommit());
    }

    @Test
    void testStartTransactionWithException() throws SQLException {
        doReturn(connectionMock).when(jdbcTransactionUtilInjectedMock).getConnection();
        doThrow(SQLException.class).when(connectionMock).setAutoCommit(anyBoolean());
        assertThrows(SQLException.class, () -> connectionMock.setAutoCommit(anyBoolean()));
        assertThrows(TransactionHandlingException.class, jdbcTransactionUtilInjectedMock::startTransaction);
    }

    @Test
    void testCommitTransaction() throws SQLException {
        jdbcTransactionUtil.startTransaction();
        Connection connection = jdbcTransactionUtil.getConnection();
        assertFalse(connection.getAutoCommit());
        jdbcTransactionUtil.commitTransaction();
        assertTrue(connection.getAutoCommit());
        assertTrue(connection.isClosed());
    }

    @Test
    void testCommitTransactionWithException() {
        doAnswer(invocationOnMock -> {
            throw new SQLException();
        }).when(jdbcTransactionUtilInjectedMock).getConnection();
        assertThrows(TransactionHandlingException.class, jdbcTransactionUtilInjectedMock::commitTransaction);
    }

    @Test
    void testRollbackTransaction() throws SQLException {
        jdbcTransactionUtil.startTransaction();
        Connection connection = jdbcTransactionUtil.getConnection();
        assertFalse(connection.getAutoCommit());
        jdbcTransactionUtil.rollbackTransaction();
        assertTrue(connection.getAutoCommit());
        assertTrue(connection.isClosed());
    }

    @Test
    void testTransactionWithException() {
        doAnswer(invocationOnMock -> {
            throw new SQLException();
        }).when(jdbcTransactionUtilInjectedMock).getConnection();
        assertThrows(TransactionHandlingException.class, jdbcTransactionUtilInjectedMock::rollbackTransaction);
    }
}
