package io.reactivestax.repository;

import io.reactivestax.entity.JournalEntry;

public interface JournalEntryRepository {
    Long insertIntoJournalEntry(JournalEntry journalEntry);

    void updateJournalEntryStatus(Long journalEntryId);
}