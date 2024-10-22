package io.reactivestax.consumer.repository;

import io.reactivestax.consumer.type.entity.JournalEntry;

public interface JournalEntryRepository {
    Long insertIntoJournalEntry(JournalEntry journalEntry);

    void updateJournalEntryStatus(Long journalEntryId);
}