package io.reactivestax.service;

import io.reactivestax.model.JournalEntry;

import java.sql.SQLException;

public interface ProcessTradeTransaction {
    void journalEntryTransaction(String[] payloadArr, String cusip) throws SQLException;
}
