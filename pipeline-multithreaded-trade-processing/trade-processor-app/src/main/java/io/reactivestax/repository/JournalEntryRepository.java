package io.reactivestax.repository;

import io.reactivestax.entity.JournalEntry;

public interface JournalEntryRepository {
    void insertIntoJournalEntry(JournalEntry journalEntry);

    void updateJournalEntryStatus(int journalEntryId);
}