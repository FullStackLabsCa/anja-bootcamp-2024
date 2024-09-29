package io.reactivestax.service;

import io.reactivestax.model.JournalEntry;
import io.reactivestax.repository.TradeRepository;

import java.sql.Connection;
import java.sql.SQLException;

public interface ProcessTradeTransaction {
    JournalEntry journalEntryTransaction(String[] payloadArr, String cusip, TradeRepository tradeRepository, Connection connection) throws SQLException;

    void positionTransaction(JournalEntry journalEntry, TradeRepository tradeRepository, Connection connection) throws SQLException;
}
