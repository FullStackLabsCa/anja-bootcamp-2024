package io.reactivestax.repository;

import java.util.Optional;

import io.reactivestax.type.dto.JournalEntryDTO;

public interface JournalEntryRepository {
    Optional<Long> saveJournalEntry(JournalEntryDTO journalEntry);

    void updateJournalEntryStatus(Long journalEntryId);
}