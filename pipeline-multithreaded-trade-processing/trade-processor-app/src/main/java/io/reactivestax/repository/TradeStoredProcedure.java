package io.reactivestax.repository;

import io.reactivestax.model.JournalEntry;

import java.sql.Connection;
import java.sql.SQLException;

public interface TradeStoredProcedure {
    int callTradeStoredProcedure(JournalEntry journalEntry, Connection connection) throws SQLException;
}
