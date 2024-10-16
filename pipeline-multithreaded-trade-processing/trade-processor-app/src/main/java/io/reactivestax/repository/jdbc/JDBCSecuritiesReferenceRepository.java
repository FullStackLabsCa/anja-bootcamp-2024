package io.reactivestax.repository.jdbc;

import io.reactivestax.repository.LookupSecuritiesRepository;

public class JDBCSecuritiesReferenceRepository implements LookupSecuritiesRepository {
    private static JDBCSecuritiesReferenceRepository instance;
    private static final String LOOKUP_SECURITIES_QUERY = "Select 1 from securities_reference where cusip = ?";

    private JDBCSecuritiesReferenceRepository() {
    }

    public static synchronized JDBCSecuritiesReferenceRepository getInstance() {
        if (instance == null) {
            instance = new JDBCSecuritiesReferenceRepository();
        }
        return instance;
    }

    @Override
    public boolean lookupSecurities(String cusip) {
        boolean validSecurity = false;
//        try (
//        PreparedStatement preparedStatement = connection.prepareStatement(LOOKUP_SECURITIES_QUERY)) {
//            preparedStatement.setString(1, cusip);
//            ResultSet resultSet = preparedStatement.executeQuery();
//            if (resultSet.next()) validSecurity = true;
//        }

        return validSecurity;
    }
}