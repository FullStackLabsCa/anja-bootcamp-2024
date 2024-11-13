package io.reactivestax.repository;

import java.util.Optional;

import io.reactivestax.repository.hibernate.entity.JournalEntry;
import io.reactivestax.type.dto.JournalEntryDTO;

public interface JournalEntryRepository {
    Optional<Long> saveJournalEntry(JournalEntryDTO journalEntry);

    void updateJournalEntryStatus(Long journalEntryId);

    JournalEntry findJournalEntryByJournalEntryId(Long journalEntryId);

    JournalEntry findJournalEntryByJournalEntry(io.reactivestax.type.dto.JournalEntryDTO journalEntryDTO);

}