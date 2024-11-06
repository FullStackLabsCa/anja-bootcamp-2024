package io.reactivestax.repository;

import io.reactivestax.type.dto.JournalEntry;

import java.util.Optional;

public interface JournalEntryRepository {
    Optional<Long> insertIntoJournalEntry(JournalEntry journalEntry);

    void updateJournalEntryStatus(Long journalEntryId);
}