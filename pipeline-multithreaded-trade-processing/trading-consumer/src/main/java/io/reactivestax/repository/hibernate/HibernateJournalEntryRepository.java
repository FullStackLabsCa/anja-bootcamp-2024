package io.reactivestax.repository.hibernate;

import java.sql.Timestamp;
import java.util.Optional;

import lombok.extern.log4j.Log4j2;
import org.hibernate.Session;

import io.reactivestax.repository.JournalEntryRepository;
import io.reactivestax.repository.hibernate.entity.JournalEntry;
import io.reactivestax.type.enums.Direction;
import io.reactivestax.type.enums.PostedStatus;
import io.reactivestax.util.database.hibernate.HibernateTransactionUtil;

@Log4j2
public class HibernateJournalEntryRepository implements JournalEntryRepository {
    private static HibernateJournalEntryRepository instance;
    private static final String JOURNAL_ENTRY_QUERY = """
            from JournalEntry where tradeId = :tradeId
            and accountNumber = :accountNumber and securityCusip = :securityCusip
            and direction = :direction and quantity = :quantity
            
            """;

    private HibernateJournalEntryRepository() {
    }

    public static synchronized HibernateJournalEntryRepository getInstance() {
        if (instance == null) {
            instance = new HibernateJournalEntryRepository();
        }
        return instance;
    }

    @Override
    public Optional<Long> saveJournalEntry(io.reactivestax.type.dto.JournalEntryDTO journalEntry) {
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

    @Override
    public JournalEntry findJournalEntryByJournalEntryId(Long journalEntryId) {
        Session session = HibernateTransactionUtil.getInstance().getConnection();
        return session.get(JournalEntry.class, journalEntryId);
    }

    @Override
    public JournalEntry findJournalEntryByJournalEntryDetails(io.reactivestax.type.dto.JournalEntryDTO journalEntryDTO) {
        log.info(() -> "Finding journal entry by journal entry");
        Session session = HibernateTransactionUtil.getInstance().getConnection();
        return session.createQuery(JOURNAL_ENTRY_QUERY, JournalEntry.class)
                .setParameter("tradeId", journalEntryDTO.getTradeId())
                .setParameter("accountNumber", journalEntryDTO.getAccountNumber())
                .setParameter("securityCusip", journalEntryDTO.getSecurityCusip())
                .setParameter("direction", Direction.valueOf(journalEntryDTO.getDirection()))
                .setParameter("quantity", journalEntryDTO.getQuantity())
                //.setParameter("transactionTimestamp", journalEntryDTO.getTransactionTimestamp())
                .uniqueResult();
    }

    private JournalEntry getJournalEntryEntity(io.reactivestax.type.dto.JournalEntryDTO journalEntry) {
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
