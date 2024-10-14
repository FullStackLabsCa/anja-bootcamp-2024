package io.reactivestax.service;

import io.reactivestax.entity.JournalEntry;

public interface ProcessTradeTransaction {
    JournalEntry journalEntryTransaction(String[] payloadArr, int tradeId);

    void positionTransaction(JournalEntry journalEntry);
}
