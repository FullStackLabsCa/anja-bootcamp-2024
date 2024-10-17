package io.reactivestax.repository.hibernate;

import io.reactivestax.entity.JournalEntry;
import io.reactivestax.enums.PostedStatusEnum;
import io.reactivestax.repository.JournalEntryRepository;
import io.reactivestax.utility.database.hibernate.HibernateTransactionUtil;
import org.hibernate.Session;

public class HibernateJournalEntryRepository implements JournalEntryRepository {
    private static HibernateJournalEntryRepository instance;

    private HibernateJournalEntryRepository() {
    }

    public static synchronized HibernateJournalEntryRepository getInstance() {
        if (instance == null) {
            instance = new HibernateJournalEntryRepository();
        }
        return instance;
    }

    @Override
    public void insertIntoJournalEntry(JournalEntry journalEntry) {
        Session session = HibernateTransactionUtil.getInstance().getConnection();
        session.persist(journalEntry);
    }

    @Override
    public void updateJournalEntryStatus(int journalEntryId) {
        Session session = HibernateTransactionUtil.getInstance().getConnection();
        JournalEntry journalEntry = session.get(JournalEntry.class, journalEntryId);
        journalEntry.setPostedStatus(PostedStatusEnum.POSTED);
    }
}
