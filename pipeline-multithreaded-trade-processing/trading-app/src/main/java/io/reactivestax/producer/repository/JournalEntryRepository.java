package io.reactivestax.producer.repository;

import io.reactivestax.producer.type.entity.JournalEntry;

public interface JournalEntryRepository {
    Long insertIntoJournalEntry(JournalEntry journalEntry);

    void updateJournalEntryStatus(Long journalEntryId);
}