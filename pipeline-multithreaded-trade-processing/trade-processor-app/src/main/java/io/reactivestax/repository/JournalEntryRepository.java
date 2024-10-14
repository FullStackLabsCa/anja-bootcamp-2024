package io.reactivestax.repository;

import io.reactivestax.entity.JournalEntry;
import io.reactivestax.enums.PostedStatusEnum;
import org.hibernate.Session;

public class JournalEntryRepository implements WriteToJournalEntry {

    @Override
    public void insertIntoJournalEntry(JournalEntry journalEntry, Session session) {
        session.persist(journalEntry);
    }

    @Override
    public void updateJournalEntryStatus(int journalEntryId, Session session) {
        JournalEntry journalEntry = session.get(JournalEntry.class, journalEntryId);
        journalEntry.setPostedStatus(PostedStatusEnum.POSTED);
    }
}
