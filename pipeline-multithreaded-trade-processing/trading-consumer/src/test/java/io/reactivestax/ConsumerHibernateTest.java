package io.reactivestax;

import io.reactivestax.repository.JournalEntryRepository;
import io.reactivestax.repository.TradePayloadRepository;
import io.reactivestax.repository.hibernate.entity.JournalEntry;
import io.reactivestax.repository.hibernate.entity.SecuritiesReference;
import io.reactivestax.repository.hibernate.entity.TradePayload;
import io.reactivestax.service.TradeService;
import io.reactivestax.type.enums.Direction;
import io.reactivestax.type.enums.LookupStatus;
import io.reactivestax.type.enums.PostedStatus;
import io.reactivestax.util.ApplicationPropertiesUtils;
import io.reactivestax.util.database.ConnectionUtil;
import io.reactivestax.util.database.TransactionUtil;
import io.reactivestax.util.database.hibernate.HibernateTransactionUtil;
import io.reactivestax.util.factory.BeanFactory;
import jakarta.persistence.NoResultException;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaDelete;
import org.hibernate.Session;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class ConsumerHibernateTest {
    TradePayloadRepository tradePayloadRepository;
    JournalEntryRepository journalEntryRepository;
    ConnectionUtil<Session> connectionUtil;
    TransactionUtil transactionUtil;
    ApplicationPropertiesUtils applicationPropertiesUtils;
    TradeService tradeService;
    Logger logger = Logger.getLogger(ConsumerHibernateTest.class.getName());
    List<TradePayload> tradePayloadList = new ArrayList<>();

    @Before
    public void setUp() {
        applicationPropertiesUtils = ApplicationPropertiesUtils.getInstance("applicationHibernateTest.properties");
        applicationPropertiesUtils.loadApplicationProperties("applicationHibernateTest.properties");
        connectionUtil = HibernateTransactionUtil.getInstance();
        tradePayloadRepository = BeanFactory.getTradePayloadRepository();
        journalEntryRepository = BeanFactory.getJournalEntryRepository();
        transactionUtil = BeanFactory.getTransactionUtil();
        transactionUtil.startTransaction();
        String[] cusipArray = {"AAPL", "GOOGL", "AMZN", "MSFT", "TSLA", "NFLX", "FB", "NVDA", "JPM", "VISA", "MA", "BAC", "DIS", "INTC", "CSCO", "ORCL", "WMT", "T", "VZ", "ADBE", "CRM", "PYPL", "PFE", "XOM", "UNH"};
        for (String cusip : cusipArray) {
            SecuritiesReference securitiesReference = new SecuritiesReference();
            securitiesReference.setCusip(cusip);
            connectionUtil.getConnection().persist(securitiesReference);
        }
        String[] tradePayloadArray = {
                "TDB_000001,2024-09-19 22:16:18,TDB_CUST_5214938,TSLA,BUY,1,638.02",
                "TDB_000002,2024-09-19 22:16:18,TDB_CUST_5214938,V,BUY,1,638.02",
                "TDB_000003,2024-09-19 22:16:18,TDB_CUST_5214938,TSLA,BUY,1,638.02",
                "TDB_000004,2024-09-19 22:16:18,TDB_CUST_5214938,TSLA,BUY,1,638.02",
                "TDB_000005,2024-09-19 22:16:18,TDB_CUST_5214938,TSLA,BUY,1,638.02",
                "TDB_000006,2024-09-19 22:16:18,TDB_CUST_5214938,TSLA,BUY,1,638.02",
                "TDB_000007,2024-09-19 22:16:18,TDB_CUST_5214938,TSLA,BUY,1,638.02",
                "TDB_000008,2024-09-19 22:16:18,TDB_CUST_5214938,TSLA,BUY,1,638.02",
                "TDB_000009,2024-09-19 22:16:18,TDB_CUST_5214938,TSLA,BUY,1,638.02",
                "TDB_000010,2024-09-19 22:16:18,TDB_CUST_5214938,TSLA,BUY,1,638.02"
        };

        for (String tradePayloadStr : tradePayloadArray) {
            TradePayload tradePayload = new TradePayload();
            tradePayload.setTradeNumber(tradePayloadStr.split(",")[0]);
            tradePayload.setPayload(tradePayloadStr);
            connectionUtil.getConnection().persist(tradePayload);
            tradePayloadList.add(tradePayload);
        }
        transactionUtil.commitTransaction();
    }

    @After
    public void cleanUp() {
        transactionUtil.startTransaction();
        Session session = connectionUtil.getConnection();
        CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
        CriteriaDelete<io.reactivestax.repository.hibernate.entity.TradePayload> criteriaDeleteTradePayload = criteriaBuilder.createCriteriaDelete(io.reactivestax.repository.hibernate.entity.TradePayload.class);
        session.createMutationQuery(criteriaDeleteTradePayload).executeUpdate();
        CriteriaDelete<io.reactivestax.repository.hibernate.entity.JournalEntry> criteriaDeleteJournalEntry =
                criteriaBuilder.createCriteriaDelete(io.reactivestax.repository.hibernate.entity.JournalEntry.class);
        session.createMutationQuery(criteriaDeleteJournalEntry).executeUpdate();
        CriteriaDelete<io.reactivestax.repository.hibernate.entity.Position> criteriaDeletePositions =
                criteriaBuilder.createCriteriaDelete(io.reactivestax.repository.hibernate.entity.Position.class);
        session.createMutationQuery(criteriaDeletePositions).executeUpdate();
        CriteriaDelete<io.reactivestax.repository.hibernate.entity.SecuritiesReference> criteriaDeleteSecuritiesReference =
                criteriaBuilder.createCriteriaDelete(io.reactivestax.repository.hibernate.entity.SecuritiesReference.class);
        session.createMutationQuery(criteriaDeleteSecuritiesReference).executeUpdate();
        transactionUtil.commitTransaction();
    }

    @Test
    public void testReadRawPayloadWithValidTradeNumber() {
        io.reactivestax.type.dto.TradePayload tradePayloadDto = tradePayloadRepository.readRawPayload("TDB_000001");
        Assert.assertEquals("TDB_000001,2024-09-19 22:16:18,TDB_CUST_5214938,TSLA,BUY,1,638.02", tradePayloadDto.getPayload());
    }

    @Test(expected = NoResultException.class)
    public void testReadRawPayloadWithInvalidTradeNumber() {
        tradePayloadRepository.readRawPayload("TDB_000020");
    }

    @Test
    public void testUpdateTradePayloadLookupStatus() {
        Long id = tradePayloadList.get(0).getId();
        tradePayloadRepository.updateTradePayloadLookupStatus(true, id);
        transactionUtil.startTransaction();
        Session session = connectionUtil.getConnection();
        TradePayload tradePayload = session.get(TradePayload.class, id);
        session.getTransaction().rollback();
        Assert.assertEquals(LookupStatus.PASS, tradePayload.getLookupStatus());
    }

    @Test(expected = NullPointerException.class)
    public void testUpdateTradePayloadLookupStatusWithInvalidId() {
        tradePayloadRepository.updateTradePayloadLookupStatus(true, 0L);
    }

    @Test
    public void testUpdateTradePayloadPostedStatus() {
        Long id = tradePayloadList.get(0).getId();
        tradePayloadRepository.updateTradePayloadPostedStatus(id);
        transactionUtil.startTransaction();
        Session session = connectionUtil.getConnection();
        TradePayload tradePayload = session.get(TradePayload.class, id);
        session.getTransaction().rollback();
        Assert.assertEquals(PostedStatus.POSTED, tradePayload.getJournalEntryStatus());
    }

    @Test
    public void testInsertIntoJournalEntry(){
        io.reactivestax.type.dto.JournalEntry journalEntryDto = new io.reactivestax.type.dto.JournalEntry();
        journalEntryDto.setTradeId("TDB_000001");
        journalEntryDto.setAccountNumber("TDB_CUST_5214938");
        journalEntryDto.setSecurityCusip("TSLA");
        journalEntryDto.setQuantity(1);
        journalEntryDto.setDirection(Direction.BUY.name());
        journalEntryDto.setTransactionTimestamp("2024-09-19 22:16:18");
        journalEntryRepository.insertIntoJournalEntry(journalEntryDto);
        transactionUtil.startTransaction();
        Session session= connectionUtil.getConnection();
        JournalEntry journalEntry =
                session.createQuery("from JournalEntry where tradeId = :tradeId", JournalEntry.class).setParameter("tradeId",
                journalEntryDto.getTradeId()).getSingleResult();
        transactionUtil.rollbackTransaction();
        Assert.assertEquals(journalEntryDto.getAccountNumber(), journalEntry.getAccountNumber());
    }
}
