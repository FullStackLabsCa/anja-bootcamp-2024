package io.reactivestax.repository.jdbc;

import io.reactivestax.entity.Position;
import io.reactivestax.repository.PositionsRepository;
import io.reactivestax.utility.DateTimeFormatterUtil;
import io.reactivestax.utility.database.jdbc.JDBCTransactionUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class JDBCPositionsRepository implements PositionsRepository {
    private static final String LOOKUP_POSITIONS_QUERY = "Select positions, version from positions where account_number = ? and security_cusip = ?";
    private static final String INSERT_INTO_POSITIONS_QUERY = "Insert into positions (account_number, security_cusip," +
            "positions, version) values(?, ?, ?, ?)";
    private static final String UPDATE_POSITONS_QUERY = "Update positions set positions = ?, version = ? where " +
            "account_number = ? and security_cusip = ? and version = ?";

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

//    @Override
//    public int[] lookupPositions(Position position, Connection connection) throws SQLException {
//        int[] positionsAndVersion = {0, 0};
//        try (PreparedStatement preparedStatement = connection.prepareStatement(LOOKUP_POSITIONS_QUERY)) {
//            preparedStatement.setString(1, position.getAccountNumber());
//            preparedStatement.setString(2, position.getSecurityCusip());
//            ResultSet resultSet = preparedStatement.executeQuery();
//            if (resultSet.next()) {
//                positionsAndVersion[0] = resultSet.getInt("positions");
//                positionsAndVersion[1] = resultSet.getInt("version");
//            }
//        }
//        return positionsAndVersion;
//    }

//    @Override
//    public void insertIntoPositions(Position position, Connection connection) throws SQLException {
//        try (PreparedStatement preparedStatement = connection.prepareStatement(INSERT_INTO_POSITIONS_QUERY)) {
//            preparedStatement.setString(1, position.getAccountNumber());
//            preparedStatement.setString(2, position.getSecurityCusip());
//            preparedStatement.setInt(3, position.getPositions());
//            preparedStatement.setInt(4, position.getVersion() + 1);
//            preparedStatement.execute();
//        }
//    }

//    @Override
//    public void updatePositions(Position position, Connection connection) throws SQLException {
//        try (PreparedStatement preparedStatement = connection.prepareStatement(UPDATE_POSITONS_QUERY)) {
//            preparedStatement.setInt(1, position.getPositions());
//            preparedStatement.setInt(2, position.getVersion() + 1);
//            preparedStatement.setString(4, position.getSecurityCusip());
//            preparedStatement.setInt(5, position.getVersion());
//            preparedStatement.execute();
//        }
//    }

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
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
