package io.reactivestax.consumer.service;

import io.reactivestax.consumer.type.entity.JournalEntry;

import java.io.IOException;

public interface TradeProcessor {
    void processTrade(String tradeId) throws InterruptedException, IOException;

    JournalEntry journalEntryTransaction(String[] payloadArr, Long tradeId);

    void positionTransaction(JournalEntry journalEntry);
}
