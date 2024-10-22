package io.reactivestax.producer.service;

import io.reactivestax.producer.type.entity.JournalEntry;

import java.io.IOException;

public interface TradeProcessorService {
    void processTrade(String tradeId) throws InterruptedException, IOException;

    JournalEntry journalEntryTransaction(String[] payloadArr, Long tradeId);

    void positionTransaction(JournalEntry journalEntry);
}
