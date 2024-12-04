package io.reactivestax.repository.hibernate;

import io.reactivestax.repository.hibernate.entity.JournalEntry;
import io.reactivestax.type.enums.PostedStatus;
import io.reactivestax.util.ApplicationPropertiesUtils;
import io.reactivestax.util.database.hibernate.HibernateTransactionUtil;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaDelete;
import org.hibernate.Session;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;
import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class HibernateJournalEntryRepositoryTest {
    private final HibernateJournalEntryRepository hibernateJournalEntryRepository =
            HibernateJournalEntryRepository.getInstance();
    private final HibernateTransactionUtil hibernateTransactionUtil = HibernateTransactionUtil.getInstance();

    @BeforeEach
    void setUp() {
        ApplicationPropertiesUtils applicationPropertiesUtils = ApplicationPropertiesUtils.getInstance(
                "applicationHibernateTest.properties");
        applicationPropertiesUtils.loadApplicationProperties("applicationHibernateTest.properties");
    }

    @AfterEach
    void tearDown() {
        hibernateTransactionUtil.startTransaction();
        Session session = hibernateTransactionUtil.getConnection();
        CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
        CriteriaDelete<JournalEntry> journalEntryCriteriaDelete = criteriaBuilder.createCriteriaDelete(io.reactivestax.repository.hibernate.entity.JournalEntry.class);
        session.createMutationQuery(journalEntryCriteriaDelete).executeUpdate();
        hibernateTransactionUtil.commitTransaction();
    }

    private final Supplier<io.reactivestax.type.dto.JournalEntry> journalEntrySupplier =
            () -> io.reactivestax.type.dto.JournalEntry.builder()
            .accountNumber("TDB_CUST_5214938")
            .securityCusip("TSLA")
            .direction("BUY")
            .tradeId("TDB_000001")
            .quantity(10)
            .transactionTimestamp("2024-09-19 22:16:18")
            .build();

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