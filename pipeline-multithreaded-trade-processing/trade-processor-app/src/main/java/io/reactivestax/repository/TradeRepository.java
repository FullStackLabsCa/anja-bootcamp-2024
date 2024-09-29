package io.reactivestax.repository;

import io.reactivestax.model.JournalEntry;
import io.reactivestax.model.Position;
import io.reactivestax.model.RawPayload;

import java.sql.*;

public class TradeRepository implements ReadAndWriteTradePayload, LookupSecurities, ReadAndUpsertPositions, WriteToJournalEntry {

    @Override
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

    @Override
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

    @Override
    public boolean lookupSecurities(String cusip, Connection connection) throws SQLException {
        boolean validSecurity = false;
        String query = "Select 1 from securities_reference where cusip = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, cusip);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) validSecurity = true;
        }

        return validSecurity;
    }

    @Override
    public void insertIntoJournalEntry(JournalEntry journalEntry, Connection connection) throws SQLException {
        String query = "Insert into journal_entry (trade_id, account_number, security_cusip, direction, quantity, " +
                "posted_status, transaction_time) values(?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            connection.setAutoCommit(false);
            preparedStatement.setString(1, journalEntry.getTradeId());
            preparedStatement.setString(2, journalEntry.getAccountNumber());
            preparedStatement.setString(3, journalEntry.getSecurityCusip());
            preparedStatement.setString(4, journalEntry.getDirection());
            preparedStatement.setInt(5, journalEntry.getQuantity());
            preparedStatement.setString(6, journalEntry.getPostedStatus());
            preparedStatement.setTimestamp(7, Timestamp.valueOf(journalEntry.getTransactionTime()));
            preparedStatement.execute();
        }
    }

    @Override
    public int[] lookupPositions(Position position, Connection connection) throws SQLException {
        String query = "Select positions, version from positions where account_number = ? and security_cusip = ?";
        int[] positionsAndVersion = {0, 0};
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, position.getAccountNumber());
            preparedStatement.setString(2, position.getSecurityCusip());
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                positionsAndVersion[0] = resultSet.getInt("positions");
                positionsAndVersion[1] = resultSet.getInt("version");
            }
        }
        return positionsAndVersion;
    }

    @Override
    public void insertIntoPositions(Position position, Connection connection) throws SQLException {
        String query = "Insert into positions (account_number, security_cusip, positions, version) values(?, ?, ?, ?)";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, position.getAccountNumber());
            preparedStatement.setString(2, position.getSecurityCusip());
            preparedStatement.setInt(3, position.getPositions());
            preparedStatement.setInt(4, position.getVersion() + 1);
            preparedStatement.execute();
        }
    }

    @Override
    public void updatePositions(Position position, Connection connection) throws SQLException {
        String query = "Update positions set positions = ?, version = ? where account_number = ? and security_cusip =" +
                " ? and version = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, position.getPositions());
            preparedStatement.setInt(2, position.getVersion() + 1);
            preparedStatement.setString(3, position.getAccountNumber());
            preparedStatement.setString(4, position.getSecurityCusip());
            preparedStatement.setInt(5, position.getVersion());
            preparedStatement.execute();
        }
    }

    @Override
    public void updateJournalEntryStatus(String tradeId, Connection connection) throws SQLException {
        String query = "Update journal_entry set posted_status = 'POSTED' where trade_id = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, tradeId);
            preparedStatement.execute();
            connection.commit();
            connection.setAutoCommit(true);
        }
    }
}
