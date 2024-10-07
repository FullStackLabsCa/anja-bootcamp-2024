package io.reactivestax.repository;

import io.reactivestax.model.JournalEntry;

import java.sql.*;

public class TradeStoredProcedureRepository {

    public void callTradeStoredProcedure(JournalEntry journalEntry, Connection connection) throws SQLException {
        String tradeStoredProcedure = "Call trade_procedure(?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try(CallableStatement statement = connection.prepareCall(tradeStoredProcedure)) {
            statement.setString(1, journalEntry.accountNumber());
            statement.setString(2, journalEntry.securityCusip());
            statement.setString(3, journalEntry.direction());
            statement.setInt(4, journalEntry.quantity());
            statement.setString(5, journalEntry.postedStatus());
            statement.setTimestamp(6, Timestamp.valueOf(journalEntry.transactionTime()));
            statement.setString(7, journalEntry.tradeId());

            statement.registerOutParameter(8, Types.INTEGER);
            statement.registerOutParameter(9, Types.VARCHAR);
            statement.execute();
            int errorCode = statement.getInt(8);
            String errorMessage = statement.getString(9);
            if(errorCode !=0) {
                System.out.println(errorMessage);
                System.out.println(errorCode);
            }
            connection.commit();
        }
    }
}
