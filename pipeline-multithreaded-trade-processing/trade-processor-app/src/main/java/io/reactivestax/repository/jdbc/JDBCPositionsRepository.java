package io.reactivestax.repository.jdbc;

import io.reactivestax.entity.Position;
import io.reactivestax.exceptions.OptimisticLockingException;
import io.reactivestax.repository.PositionsRepository;
import io.reactivestax.utility.DateTimeFormatterUtil;
import io.reactivestax.utility.database.jdbc.JDBCTransactionUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class JDBCPositionsRepository implements PositionsRepository {

    private static final String UPSERT_POSITIONS_QUERY = "Insert into positions (account_number, security_cusip, " +
            "holding, version, created_at, updated_at) values(?, ?, ?, ?, ?, ?) on duplicate key update holding = " +
            "holding + ?, version = version + 1, updated_at = ?";

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
        Connection connection = JDBCTransactionUtil.getInstance().getConnection();
        position.setCreatedAt(DateTimeFormatterUtil.formattedTimestamp());
        position.setUpdatedAt(DateTimeFormatterUtil.formattedTimestamp());
        try (PreparedStatement preparedStatement = connection.prepareStatement(UPSERT_POSITIONS_QUERY)) {
            preparedStatement.setString(1, position.getPositionCompositeKey().getAccountNumber());
            preparedStatement.setString(2, position.getPositionCompositeKey().getSecurityCusip());
            preparedStatement.setInt(3, position.getHolding());
            preparedStatement.setInt(4, 0);
            preparedStatement.setTimestamp(5, position.getCreatedAt());
            preparedStatement.setTimestamp(6, position.getUpdatedAt());
            preparedStatement.setInt(7, position.getHolding());
            preparedStatement.setTimestamp(8, position.getUpdatedAt());
            preparedStatement.execute();
        } catch (SQLException e) {
            throw new OptimisticLockingException("Optimistic locking", e);
        }
    }
}
