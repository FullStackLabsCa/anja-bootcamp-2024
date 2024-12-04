package io.reactivestax.repository.hibernate;

import io.reactivestax.repository.hibernate.entity.TradePayload;
import io.reactivestax.type.enums.LookupStatus;
import io.reactivestax.type.enums.PostedStatus;
import io.reactivestax.type.enums.ValidityStatus;
import io.reactivestax.util.ApplicationPropertiesUtils;
import io.reactivestax.util.database.hibernate.HibernateTransactionUtil;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaDelete;
import org.hibernate.Session;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class HibernateTradePayloadRepositoryTest {

    private final HibernateTradePayloadRepository hibernateTradePayloadRepository =
            HibernateTradePayloadRepository.getInstance();
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
        CriteriaDelete<TradePayload> criteriaDeleteTradePayload = criteriaBuilder.createCriteriaDelete(io.reactivestax.repository.hibernate.entity.TradePayload.class);
        session.createMutationQuery(criteriaDeleteTradePayload).executeUpdate();
        hibernateTransactionUtil.commitTransaction();
    }

    private final Supplier<TradePayload> buyTradePayloadEntity = () -> TradePayload.builder()
            .tradeNumber("TDB_000001")
            .payload("TDB_000001,2024-09-19 22:16:18,TDB_CUST_5214938,TSLA,BUY,1,638.02")
            .validityStatus(ValidityStatus.VALID)
            .lookupStatus(LookupStatus.NOT_CHECKED)
            .journalEntryStatus(PostedStatus.NOT_POSTED)
            .build();

    @Test
    void testReadRawPayload() {
        TradePayload tradePayloadEntity = buyTradePayloadEntity.get();
        hibernateTransactionUtil.startTransaction();
        insertTradePayload();
        io.reactivestax.type.dto.TradePayload tradePayloadDto = hibernateTradePayloadRepository.readRawPayload(
                "TDB_000001");
        assertNotNull(tradePayloadDto);
        assertEquals(tradePayloadEntity.getTradeNumber(), tradePayloadDto.getTradeNumber());
        assertEquals(tradePayloadEntity.getPayload(), tradePayloadDto.getPayload());
        assertEquals(tradePayloadEntity.getValidityStatus().name(), tradePayloadDto.getValidityStatus());
        assertEquals(tradePayloadEntity.getLookupStatus().name(), tradePayloadDto.getLookupStatus());
        assertEquals(tradePayloadEntity.getJournalEntryStatus().name(), tradePayloadDto.getJournalEntryStatus());
        hibernateTransactionUtil.rollbackTransaction();
    }

    @Test
    void testUpdateTradePayloadLookupStatus() {
        hibernateTransactionUtil.startTransaction();
        Long id = insertTradePayload();
        hibernateTradePayloadRepository.updateTradePayloadLookupStatus(true, 1L);
        io.reactivestax.type.dto.TradePayload tradePayloadDto = hibernateTradePayloadRepository.readRawPayload("TDB_000001");
        assertNotNull(tradePayloadDto);
        assertEquals(LookupStatus.PASS.name(), tradePayloadDto.getLookupStatus());
        hibernateTransactionUtil.rollbackTransaction();
    }

    @Test
    void testUpdateTradePayloadPostedStatus(){
        hibernateTransactionUtil.startTransaction();
        Long id = insertTradePayload();
        hibernateTradePayloadRepository.updateTradePayloadPostedStatus(id);
        io.reactivestax.type.dto.TradePayload tradePayloadDto = hibernateTradePayloadRepository.readRawPayload("TDB_000001");
        assertNotNull(tradePayloadDto);
        assertEquals(PostedStatus.POSTED.name(), tradePayloadDto.getJournalEntryStatus());
        hibernateTransactionUtil.rollbackTransaction();
    }

    Long insertTradePayload() {
        TradePayload tradePayload = buyTradePayloadEntity.get();
        Session session = hibernateTransactionUtil.getConnection();
        session.persist(tradePayload);
        return tradePayload.getId();
    }
}
