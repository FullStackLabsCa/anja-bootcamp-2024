package io.reactivestax.repository.jdbc;

import io.reactivestax.exception.OptimisticLockingException;
import io.reactivestax.repository.LookupSecuritiesRepository;
import io.reactivestax.util.database.jdbc.JDBCTransactionUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

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
        Connection connection = JDBCTransactionUtil.getInstance().getConnection();
        try (PreparedStatement preparedStatement = connection.prepareStatement(LOOKUP_SECURITIES_QUERY)) {
            preparedStatement.setString(1, cusip);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) validSecurity = true;
        } catch (SQLException e) {
            throw new OptimisticLockingException("Optimistic locking", e);
        }

        return validSecurity;
    }
}