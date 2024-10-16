package io.reactivestax.repository;

import io.reactivestax.entity.JournalEntry;
import org.hibernate.Session;

public interface JournalEntryRepository {
    void insertIntoJournalEntry(JournalEntry journalEntry);

    void updateJournalEntryStatus(int journalEntryId);
}