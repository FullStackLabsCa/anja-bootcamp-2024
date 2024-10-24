package io.reactivestax.service;

import io.reactivestax.type.entity.JournalEntry;

import java.io.IOException;

public interface TradeProcessor {
    void processTrade(String tradeId, String queueName) throws InterruptedException, IOException;

    JournalEntry journalEntryTransaction(String[] payloadArr, Long tradeId);

    void positionTransaction(JournalEntry journalEntry);
}
