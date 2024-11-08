package io.reactivestax.task;

import java.io.IOException;

import io.reactivestax.type.dto.JournalEntryDTO;

public interface TradeProcessor {
    void processTrade(String tradeId, String queueName) throws InterruptedException, IOException;

    JournalEntryDTO executeJournalEntryTransaction(String[] payloadArr, Long tradeId);

    void executePositionTransaction(JournalEntryDTO journalEntry);
}
