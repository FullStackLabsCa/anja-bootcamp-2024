package io.reactivestax.repository.hibernate;

import io.reactivestax.repository.JournalEntryRepository;
import io.reactivestax.repository.hibernate.entity.JournalEntry;
import io.reactivestax.type.enums.Direction;
import io.reactivestax.type.enums.PostedStatus;
import io.reactivestax.util.database.hibernate.HibernateTransactionUtil;
import org.hibernate.Session;

import java.sql.Timestamp;
import java.util.Optional;

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
    public Optional<Long> insertIntoJournalEntry(io.reactivestax.type.dto.JournalEntry journalEntry) {
        JournalEntry journalEntryEntity = getJournalEntryEntity(journalEntry);
        Session session = HibernateTransactionUtil.getInstance().getConnection();
        session.persist(journalEntryEntity);

        return Optional.ofNullable(journalEntryEntity.getId());
    }

    @Override
    public void updateJournalEntryStatus(Long journalEntryId) {
        Session session = HibernateTransactionUtil.getInstance().getConnection();
        JournalEntry journalEntry = session.get(JournalEntry.class, journalEntryId);
        journalEntry.setPostedStatus(PostedStatus.POSTED);
    }

    private JournalEntry getJournalEntryEntity(io.reactivestax.type.dto.JournalEntry journalEntry) {
        JournalEntry journalEntryEntity = new JournalEntry();
        journalEntryEntity.setTradeId(journalEntry.getTradeId());
        journalEntryEntity.setAccountNumber(journalEntry.getAccountNumber());
        journalEntryEntity.setSecurityCusip(journalEntry.getSecurityCusip());
        journalEntryEntity.setDirection(Direction.valueOf(journalEntry.getDirection()));
        journalEntryEntity.setQuantity(journalEntry.getQuantity());
        journalEntryEntity.setTransactionTimestamp(Timestamp.valueOf(journalEntry.getTransactionTimestamp()));

        return journalEntryEntity;
    }
}
