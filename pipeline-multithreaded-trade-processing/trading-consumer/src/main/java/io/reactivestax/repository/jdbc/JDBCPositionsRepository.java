package io.reactivestax.consumer.repository.jdbc;

import io.reactivestax.consumer.type.entity.Position;
import io.reactivestax.consumer.type.entity.PositionCompositeKey;
import io.reactivestax.consumer.type.exception.OptimisticLockingException;
import io.reactivestax.consumer.repository.PositionsRepository;
import io.reactivestax.consumer.util.database.jdbc.JDBCTransactionUtil;

import java.sql.*;

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
    public void upsertPosition(Position position) {
        try {
            Position position1 = selectPosition(position.getPositionCompositeKey().getAccountNumber(),
                    position.getPositionCompositeKey().getSecurityCusip());
            if (position1 != null && position1.getPositionCompositeKey() != null) {
                position.setVersion(position1.getVersion());
                updatePosition(position);
            } else insertPosition(position);
        } catch (SQLException e) {
            throw new OptimisticLockingException("Optimistic locking", e);
        }
    }

    private Position selectPosition(String accountNumber, String cusip) {
        Position position = null;
        PositionCompositeKey positionCompositeKey = new PositionCompositeKey();
        Connection connection = JDBCTransactionUtil.getInstance().getConnection();
        try (PreparedStatement preparedStatement = connection.prepareStatement(SELECT_POSITIONS_QUERY)) {
            preparedStatement.setString(1, accountNumber);
            preparedStatement.setString(2, cusip);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                position = new Position();
                position.setVersion(resultSet.getInt("version"));
                position.setCreatedTimestamp(Timestamp.valueOf(resultSet.getString("created_timestamp")));
                position.setUpdatedTimestamp(Timestamp.valueOf(resultSet.getString("updated_timestamp")));
                position.setHolding(resultSet.getLong("holding"));
                positionCompositeKey.setAccountNumber(resultSet.getString("account_number"));
                positionCompositeKey.setSecurityCusip(resultSet.getString("security_cusip"));
                position.setPositionCompositeKey(positionCompositeKey);
            }
            return position;
        } catch (SQLException e) {
            return position;
        }
    }

    private void insertPosition(Position position) throws SQLException {
        Connection connection = JDBCTransactionUtil.getInstance().getConnection();
        try (PreparedStatement preparedStatement = connection.prepareStatement(INSERT_POSITIONS_QUERY)) {
            preparedStatement.setString(1, position.getPositionCompositeKey().getAccountNumber());
            preparedStatement.setString(2, position.getPositionCompositeKey().getSecurityCusip());
            preparedStatement.setLong(3, position.getHolding());
            preparedStatement.setInt(4, 0);
            preparedStatement.execute();
        }
    }

    private void updatePosition(Position position) throws SQLException {
        Connection connection = JDBCTransactionUtil.getInstance().getConnection();
        try (PreparedStatement preparedStatement = connection.prepareStatement(UPDATE_POSITIONS_QUERY)) {
            preparedStatement.setLong(1, position.getHolding());
            preparedStatement.setInt(2, position.getVersion());
            preparedStatement.setString(3, position.getPositionCompositeKey().getAccountNumber());
            preparedStatement.setString(4, position.getPositionCompositeKey().getSecurityCusip());
            int affectedRows = preparedStatement.executeUpdate();
            if (affectedRows == 0) {
                throw new OptimisticLockingException("Optimistic lock");
            }
        }
    }
}
