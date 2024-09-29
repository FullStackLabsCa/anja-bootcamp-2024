package io.reactivestax.repository;

import io.reactivestax.model.JournalEntry;

import java.sql.Connection;
import java.sql.SQLException;

public interface WriteToJournalEntry {
    void insertIntoJournalEntry(JournalEntry journalEntry, Connection connection) throws SQLException;

    void updateJournalEntryStatus(String tradeId, Connection connection) throws SQLException;
}
