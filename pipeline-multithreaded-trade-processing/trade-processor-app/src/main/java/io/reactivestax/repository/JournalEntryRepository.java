package io.reactivestax.repository;

import io.reactivestax.model.JournalEntry;

import java.sql.*;

public class JournalEntryRepository implements WriteToJournalEntry {
private static final String INSERT_INTO_JOURNAL_ENTRY_QUERY = "Insert into journal_entry (trade_id, account_number, " +
        "security_cusip, direction, quantity, posted_status, transaction_time) values(?, ?, ?, ?, ?, ?, ?)";
private static final String UPDATE_JOURNAL_ENTRY_STATUS_QUERY = "Update journal_entry set posted_status = 'POSTED' where trade_id = ?";

    @Override
    public void insertIntoJournalEntry(JournalEntry journalEntry, Connection connection) throws SQLException {
        try (PreparedStatement preparedStatement = connection.prepareStatement(INSERT_INTO_JOURNAL_ENTRY_QUERY)) {
            preparedStatement.setString(1, journalEntry.tradeId());
            preparedStatement.setString(2, journalEntry.accountNumber());
            preparedStatement.setString(3, journalEntry.securityCusip());
            preparedStatement.setString(4, journalEntry.direction());
            preparedStatement.setInt(5, journalEntry.quantity());
            preparedStatement.setString(6, journalEntry.postedStatus());
            preparedStatement.setTimestamp(7, Timestamp.valueOf(journalEntry.transactionTime()));
            preparedStatement.execute();
        }
    }

    @Override
    public void updateJournalEntryStatus(String tradeId, Connection connection) throws SQLException {
        try (PreparedStatement preparedStatement = connection.prepareStatement(UPDATE_JOURNAL_ENTRY_STATUS_QUERY)) {
            preparedStatement.setString(1, tradeId);
            preparedStatement.execute();
            connection.commit();
            connection.setAutoCommit(true);
        }
    }
}
