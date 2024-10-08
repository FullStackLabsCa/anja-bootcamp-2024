package io.reactivestax.repository;

import io.reactivestax.model.JournalEntry;

import java.sql.*;

public class TradeStoredProcedureRepository implements TradeStoredProcedure {

    public int callTradeStoredProcedure(JournalEntry journalEntry, Connection connection) throws SQLException {
        String tradeStoredProcedure = "Call trade_procedure(?, ?, ?, ?, ?, ?, ?, ?)";
        int errorCode = 0;
        try (CallableStatement statement = connection.prepareCall(tradeStoredProcedure)) {
            statement.setString(1, journalEntry.accountNumber());
            statement.setString(2, journalEntry.securityCusip());
            statement.setString(3, journalEntry.direction());
            statement.setInt(4, journalEntry.quantity());
            statement.setString(5, journalEntry.postedStatus());
            statement.setTimestamp(6, Timestamp.valueOf(journalEntry.transactionTime()));
            statement.setString(7, journalEntry.tradeId());
            statement.registerOutParameter(8, Types.INTEGER);
            statement.execute();
            errorCode = statement.getInt(8);
            if (errorCode == 0) connection.commit();
            else connection.rollback();
        }
        return errorCode;
    }
}
