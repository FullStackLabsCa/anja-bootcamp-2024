package io.reactivestax.repository;

import io.reactivestax.type.entity.JournalEntry;

public interface JournalEntryRepository {
    Long insertIntoJournalEntry(JournalEntry journalEntry);

    void updateJournalEntryStatus(Long journalEntryId);
}