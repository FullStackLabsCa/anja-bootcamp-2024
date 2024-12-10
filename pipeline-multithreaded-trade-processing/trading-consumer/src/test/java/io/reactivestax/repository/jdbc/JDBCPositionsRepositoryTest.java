package io.reactivestax.repository.jdbc;

import io.reactivestax.type.dto.Position;
import io.reactivestax.type.exception.OptimisticLockingException;
import io.reactivestax.util.DbSetUpUtil;
import io.reactivestax.util.EntitySupplier;
import io.reactivestax.util.database.ConnectionUtil;
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
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Supplier;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JDBCPositionsRepositoryTest {
    private final DbSetUpUtil dbSetUpUtil = new DbSetUpUtil();
    private JDBCPositionsRepository jdbcPositionsRepository;
    private final Supplier<Position> positionSupplier = EntitySupplier.positionSupplier;
    private ConnectionUtil<Connection> connectionUtil;
    @Mock
    private JDBCTransactionUtil jdbcTransactionUtilMock;
    @Mock
    private Connection connectionMock;
    @Mock
    private PreparedStatement preparedStatementMock;
    @Mock
    private ResultSet resultSetMock;
    @InjectMocks
    private JDBCPositionsRepository jdbcPositionsRepositoryMock;

    @BeforeEach
    void setUp() throws SQLException {
        connectionUtil = JDBCTransactionUtil.getInstance();
        jdbcPositionsRepository = JDBCPositionsRepository.getInstance();
        dbSetUpUtil.createPositionsTable();
    }

    @Test
    void testInsertPosition() throws SQLException {
        Position position = positionSupplier.get();
        jdbcPositionsRepository.upsertPosition(position);
        Position position1 = readPosition(position.getAccountNumber(), position.getSecurityCusip());
        assertEquals(position.getAccountNumber(), position1.getAccountNumber());
        assertEquals(position.getSecurityCusip(), position1.getSecurityCusip());
        assertEquals(10, position.getHolding());
        assertEquals(0, position.getVersion());
    }

    @Test
    void testUpdatePosition() throws SQLException {
        Position position1 = positionSupplier.get();
        jdbcPositionsRepository.upsertPosition(position1);
        Position position2 = positionSupplier.get();
        jdbcPositionsRepository.upsertPosition(position2);
        Position position = readPosition(position1.getAccountNumber(), position1.getSecurityCusip());
        assertEquals(position.getAccountNumber(), position1.getAccountNumber());
        assertEquals(position.getSecurityCusip(), position1.getSecurityCusip());
        assertEquals(20, position.getHolding());
        assertEquals(1, position.getVersion());
    }

    @Test
    void testUpsertPositionWithException() throws SQLException {
        Position position1 = positionSupplier.get();
        jdbcPositionsRepository.upsertPosition(position1);
        try (MockedStatic<JDBCTransactionUtil> jdbcTransactionUtilMockedStatic = mockStatic(JDBCTransactionUtil.class)) {
            jdbcTransactionUtilMockedStatic.when(JDBCTransactionUtil::getInstance).thenReturn(jdbcTransactionUtilMock);
            doReturn(connectionMock).when(jdbcTransactionUtilMock).getConnection();
            doReturn(preparedStatementMock).when(connectionMock).prepareStatement(anyString());
            doReturn(resultSetMock).when(preparedStatementMock).executeQuery();
            doReturn(true).when(resultSetMock).next();
            doReturn(1).when(resultSetMock).getInt(anyString());
            doReturn(0).when(preparedStatementMock).executeUpdate();
            Position position2 = positionSupplier.get();
            assertThrows(OptimisticLockingException.class,() -> jdbcPositionsRepositoryMock.upsertPosition(position2));
        }
    }

    private Position readPosition(String accountNumber, String cusip) throws SQLException {
        Position position = null;
        String query = "Select account_number, security_cusip, holding, version from positions where account_number =" +
                " ? and security_cusip = ?";
        Connection connection = connectionUtil.getConnection();
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, accountNumber);
            preparedStatement.setString(2, cusip);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                position = new Position(resultSet.getString(1),
                        resultSet.getString(2),
                        resultSet.getLong(3),
                        resultSet.getInt(4)
                );
            }
        }
        return position;
    }
}
