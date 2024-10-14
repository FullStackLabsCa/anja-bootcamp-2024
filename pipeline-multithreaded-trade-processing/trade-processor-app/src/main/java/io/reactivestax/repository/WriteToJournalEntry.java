package io.reactivestax.repository;

import io.reactivestax.entity.JournalEntry;
import org.hibernate.Session;

public interface WriteToJournalEntry {
    void insertIntoJournalEntry(JournalEntry journalEntry , Session session);

    void updateJournalEntryStatus(int journalEntryId, Session session);
}