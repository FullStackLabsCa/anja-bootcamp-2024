package io.reactivestax.repository;

import io.reactivestax.model.JournalEntry;
import io.reactivestax.model.Position;
import io.reactivestax.model.RawPayload;

import java.sql.*;

public class TradeRepository {

    public void insertTradeRawPayload(RawPayload rawPayload, Connection connection) throws SQLException {
        String query = "Insert into trade_payloads (trade_id, status, status_reason, payload) values(?, ?, ?, ?)";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            connection.setAutoCommit(false);
            preparedStatement.setString(1, rawPayload.getTradeId());
            preparedStatement.setString(2, rawPayload.getStatus());
            preparedStatement.setString(3, rawPayload.getStatusReason());
            preparedStatement.setString(4, rawPayload.getPayload());
            preparedStatement.execute();
            connection.commit();
            connection.setAutoCommit(true);
        }
    }

    public String readRawPayload(String tradeId, Connection connection) throws SQLException {
        String payload = "";
        String query = "Select payload from trade_payloads where trade_id = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, tradeId);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                payload = resultSet.getString("payload");
            }
        }

        return payload;
    }

    public boolean lookupSecurities(String cusip, Connection connection) throws SQLException {
        boolean validSecurity = false;
        String query = "Select 1 from securities_reference where cusip = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, cusip);
            ResultSet resultSet = preparedStatement.executeQuery();
            if(resultSet.next()) validSecurity=true;
        }

        return  validSecurity;
    }

    public void insertIntoJournalEntry(JournalEntry journalEntry, Connection connection) throws SQLException {
        String query = "Insert into journal_entry (account_number, security_cusip, direction, quantity, " +
                "posted_status, transaction_time) values(?, ?, ?, ?, ?, ?)";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            connection.setAutoCommit(false);
            preparedStatement.setString(1, journalEntry.getAccountNumber());
            preparedStatement.setString(2, journalEntry.getSecurityCusip());
            preparedStatement.setString(3, journalEntry.getDirection());
            preparedStatement.setInt(4, journalEntry.getQuantity());
            preparedStatement.setString(5, journalEntry.getPostedStatus());
            preparedStatement.setTimestamp(6, Timestamp.valueOf(journalEntry.getTransactionTime()));
            preparedStatement.execute();
        }
    }

    public int lookupPositions(Position position, Connection connection) throws SQLException {
        String query = "Select quantity from positions where account_number = ? and security_cusip = ?";
        int quantity = 0;
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, position.getAccountNumber());
            preparedStatement.setString(2, position.getSecurityCusip());
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                quantity = resultSet.getInt("quantity");
            }
        }
        return quantity;
    }

    public void upsertIntoPositions(Position position, Connection connection) throws SQLException {
        String query = "Insert into positions (account_number, security_cusip, quantity) values(?, ?, ?) on duplicate" +
                " key update quantity = values(quantity)";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, position.getAccountNumber());
            preparedStatement.setString(2, position.getSecurityCusip());
            preparedStatement.setInt(3, position.getQuantity());
            preparedStatement.execute();
            connection.commit();
            connection.setAutoCommit(true);
        }
    }
}
