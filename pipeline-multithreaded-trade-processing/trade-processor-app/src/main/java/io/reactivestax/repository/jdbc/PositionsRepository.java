//package io.reactivestax.repository.jdbc;
//
//import io.reactivestax.model.Position;
//
//import java.sql.Connection;
//import java.sql.PreparedStatement;
//import java.sql.ResultSet;
//import java.sql.SQLException;
//
//public class PositionsRepository implements ReadAndUpsertPositions {
//    private static final String LOOKUP_POSITIONS_QUERY = "Select positions, version from positions where account_number = ? and security_cusip = ?";
//    private static final String INSERT_INTO_POSITIONS_QUERY = "Insert into positions (account_number, security_cusip," +
//            "positions, version) values(?, ?, ?, ?)";
//    private static final String UPDATE_POSITONS_QUERY = "Update positions set positions = ?, version = ? where " +
//            "account_number = ? and security_cusip = ? and version = ?";
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
//
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
//
//    @Override
//    public void updatePositions(Position position, Connection connection) throws SQLException {
//        try (PreparedStatement preparedStatement = connection.prepareStatement(UPDATE_POSITONS_QUERY)) {
//            preparedStatement.setInt(1, position.getPositions());
//            preparedStatement.setInt(2, position.getVersion() + 1);
//            preparedStatement.setString(3, position.getAccountNumber());
//            preparedStatement.setString(4, position.getSecurityCusip());
//            preparedStatement.setInt(5, position.getVersion());
//            preparedStatement.execute();
//        }
//    }
//}
