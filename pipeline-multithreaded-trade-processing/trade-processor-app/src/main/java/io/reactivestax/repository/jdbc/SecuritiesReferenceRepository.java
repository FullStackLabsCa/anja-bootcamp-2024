//package io.reactivestax.repository.jdbc;
//
//import java.sql.Connection;
//import java.sql.PreparedStatement;
//import java.sql.ResultSet;
//import java.sql.SQLException;
//
//public class SecuritiesReferenceRepository implements LookupSecurities {
//    private static final String LOOKUP_SECURITIES_QUERY = "Select 1 from securities_reference where cusip = ?";
//
//    @Override
//    public boolean lookupSecurities(String cusip, Connection connection) throws SQLException {
//        boolean validSecurity = false;
//        try (PreparedStatement preparedStatement = connection.prepareStatement(LOOKUP_SECURITIES_QUERY)) {
//            preparedStatement.setString(1, cusip);
//            ResultSet resultSet = preparedStatement.executeQuery();
//            if (resultSet.next()) validSecurity = true;
//        }
//
//        return validSecurity;
//    }
//}