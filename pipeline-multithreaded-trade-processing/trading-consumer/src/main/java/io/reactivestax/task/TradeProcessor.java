package io.reactivestax.task;

import io.reactivestax.type.dto.JournalEntry;

import java.io.IOException;

public interface TradeProcessor {
    void processTrade(String tradeId, String queueName) throws InterruptedException, IOException;

    JournalEntry journalEntryTransaction(String[] payloadArr, Long tradeId);

    void positionTransaction(JournalEntry journalEntry);
}
