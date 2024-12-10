package io.reactivestax.repository.hibernate;

import io.reactivestax.repository.hibernate.entity.JournalEntry;
import io.reactivestax.type.enums.PostedStatus;
import io.reactivestax.util.ApplicationPropertiesUtils;
import io.reactivestax.util.DbSetUpUtil;
import io.reactivestax.util.EntitySupplier;
import io.reactivestax.util.database.hibernate.HibernateTransactionUtil;
import org.hibernate.Session;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.util.Optional;
import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class HibernateJournalEntryRepositoryTest {
    private final HibernateJournalEntryRepository hibernateJournalEntryRepository =
            HibernateJournalEntryRepository.getInstance();
    private final HibernateTransactionUtil hibernateTransactionUtil = HibernateTransactionUtil.getInstance();
    private final Supplier<io.reactivestax.type.dto.JournalEntry> journalEntrySupplier = EntitySupplier.journalEntrySupplier;
    private final DbSetUpUtil dbSetUpUtil = new DbSetUpUtil();

    @BeforeEach
    void setUp() throws SQLException {
        ApplicationPropertiesUtils applicationPropertiesUtils = ApplicationPropertiesUtils.getInstance(
                "applicationHibernateTest.properties");
        applicationPropertiesUtils.loadApplicationProperties("applicationHibernateTest.properties");
        dbSetUpUtil.createJournalEntryTable();
    }

    @Test
    void testInsertIntoJournalEntry() {
        hibernateTransactionUtil.startTransaction();
        Optional<Long> id = hibernateJournalEntryRepository.insertIntoJournalEntry(journalEntrySupplier.get());
        assertTrue(id.isPresent());
        hibernateTransactionUtil.rollbackTransaction();
    }

    @Test
    void testUpdateJournalEntryStatus() {
        hibernateTransactionUtil.startTransaction();
        Optional<Long> id = hibernateJournalEntryRepository.insertIntoJournalEntry(journalEntrySupplier.get());
        id.ifPresent(hibernateJournalEntryRepository::updateJournalEntryStatus);
        Session session = hibernateTransactionUtil.getConnection();
        id.ifPresent(i -> {
            JournalEntry journalEntry = session.get(JournalEntry.class, i);
            assertEquals(PostedStatus.POSTED, journalEntry.getPostedStatus());
        });
        hibernateTransactionUtil.rollbackTransaction();
    }
}