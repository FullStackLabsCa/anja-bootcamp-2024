package io.reactivestax.repository;

import io.reactivestax.model.JournalEntry;
import io.reactivestax.model.RawPayload;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

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
        }
    }

    public String readRawPayload(String tradeId, Connection connection) throws SQLException {
        String payload = "";
        String query = "Select payload from trade_payloads where trade_id = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, tradeId);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                payload = resultSet.getString("payload");
            }
        }
        return payload;
    }

    public void lookupSecurities(String cusip, Connection connection) throws SQLException {
        String query = "Select 1 from securities_reference where cusip = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, cusip);
            ResultSet resultSet = preparedStatement.executeQuery();
        }
    }

    public void insertIntoJournalEntry(JournalEntry journalEntry, Connection connection) throws SQLException {
        String query = "Insert into journal_entry (account_number, security_cusip, direction, quantity, " +
                "posted_status, transaction_time) values(?, ?, ?, ?, ?, ?)";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {

        }
    }

    public void lookupPositions(Connection connection) throws SQLException {
        String query = "Select * from positions where account_number = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {

        }
    }

    public void insertIntoPositons(Connection connection) throws SQLException {
        String query = "Insert into positions (account_number, security_cusip, quantity) values(?, ?, ?)";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {

        }
    }

    public void updateEntryInPositions(String direction, Connection connection) throws SQLException {
        String addPositonQuery = "Update positions set position = position + ? where ";
        String subtractPositonQuery = "Update positions set position = position - ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(direction.equals("buy") ?
                addPositonQuery : subtractPositonQuery)) {

        }
    }
}
