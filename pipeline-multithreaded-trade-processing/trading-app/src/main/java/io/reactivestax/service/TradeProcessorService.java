package io.reactivestax.service;

import io.reactivestax.entity.JournalEntry;

import java.io.IOException;

public interface TradeProcessorService {
    void processTrade(String tradeId) throws InterruptedException, IOException;

    JournalEntry journalEntryTransaction(String[] payloadArr, int tradeId);

    void positionTransaction(JournalEntry journalEntry);
}
