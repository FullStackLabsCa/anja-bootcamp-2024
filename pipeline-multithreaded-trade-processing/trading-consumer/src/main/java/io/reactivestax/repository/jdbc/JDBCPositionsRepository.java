package io.reactivestax.repository.jdbc;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import io.reactivestax.repository.PositionsRepository;
import io.reactivestax.type.exception.OptimisticLockingException;
import io.reactivestax.util.database.jdbc.JDBCTransactionUtil;

public class JDBCPositionsRepository implements PositionsRepository {
    private static final String SELECT_POSITIONS_QUERY = "Select version, created_timestamp, holding, " +
            "updated_timestamp, account_number, security_cusip from positions where account_number = ? and " +
            "security_cusip = ?";
    private static final String INSERT_POSITIONS_QUERY = "Insert into positions (account_number, security_cusip, " +
            "holding, version, created_timestamp, updated_timestamp) values(?, ?, ?, ?, NOW(), NOW())";
    private static final String UPDATE_POSITIONS_QUERY = "update positions set holding = holding + ?, version = " +
            "version + 1, updated_timestamp = NOW() WHERE version = ? AND account_number = ? AND security_cusip = ?";

    private static JDBCPositionsRepository instance;

    private JDBCPositionsRepository() {
        // private constructor to prevent instantiation
    }

    public static synchronized JDBCPositionsRepository getInstance() {
        if (instance == null) {
            instance = new JDBCPositionsRepository();
        }
        return instance;
    }

    @Override
    public void upsertPosition(io.reactivestax.type.dto.PositionDTO position) {
        try {
            Integer positionVersion = selectPositionVersion(position.getAccountNumber(), position.getSecurityCusip());
            if (positionVersion != null) {
                position.setVersion(positionVersion);
                updatePosition(position);
            } else
                insertPosition(position);
        } catch (SQLException e) {
            throw new OptimisticLockingException("Optimistic locking", e);
        }
    }

    private Integer selectPositionVersion(String accountNumber, String cusip) {
        Integer version = null;
        Connection connection = JDBCTransactionUtil.getInstance().getConnection();
        try (PreparedStatement preparedStatement = connection.prepareStatement(SELECT_POSITIONS_QUERY)) {
            preparedStatement.setString(1, accountNumber);
            preparedStatement.setString(2, cusip);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                version = resultSet.getInt("version");
            }

            return version;
        } catch (SQLException e) {
            return version;
        }
    }

    private void insertPosition(io.reactivestax.type.dto.PositionDTO position) throws SQLException {
        Connection connection = JDBCTransactionUtil.getInstance().getConnection();
        try (PreparedStatement preparedStatement = connection.prepareStatement(INSERT_POSITIONS_QUERY)) {
            preparedStatement.setString(1, position.getAccountNumber());
            preparedStatement.setString(2, position.getSecurityCusip());
            preparedStatement.setLong(3, position.getHolding());
            preparedStatement.setInt(4, 0);
            preparedStatement.execute();
        }
    }

    private void updatePosition(io.reactivestax.type.dto.PositionDTO position) throws SQLException {
        Connection connection = JDBCTransactionUtil.getInstance().getConnection();
        try (PreparedStatement preparedStatement = connection.prepareStatement(UPDATE_POSITIONS_QUERY)) {
            preparedStatement.setLong(1, position.getHolding());
            preparedStatement.setInt(2, position.getVersion());
            preparedStatement.setString(3, position.getAccountNumber());
            preparedStatement.setString(4, position.getSecurityCusip());
            int affectedRows = preparedStatement.executeUpdate();
            if (affectedRows == 0) {
                throw new OptimisticLockingException("Optimistic lock");
            }
        }
    }
}
