package io.reactivestax.repository;

import io.reactivestax.type.dto.JournalEntry;

public interface JournalEntryRepository {
    Long insertIntoJournalEntry(JournalEntry journalEntry);

    void updateJournalEntryStatus(Long journalEntryId);
}