package io.reactivestax.service;

import io.reactivestax.model.JournalEntry;

import java.sql.SQLException;

public interface ProcessTradeTransaction {
    JournalEntry journalEntryTransaction(String[] payloadArr, String cusip) throws SQLException;

    void positionTransaction(JournalEntry journalEntry) throws SQLException;
}
