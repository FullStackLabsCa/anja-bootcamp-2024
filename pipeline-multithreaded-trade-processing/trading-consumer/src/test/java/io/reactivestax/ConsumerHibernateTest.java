package io.reactivestax;

import io.reactivestax.repository.JournalEntryRepository;
import io.reactivestax.repository.LookupSecuritiesRepository;
import io.reactivestax.repository.PositionsRepository;
import io.reactivestax.repository.TradePayloadRepository;
import io.reactivestax.repository.hibernate.entity.JournalEntry;
import io.reactivestax.repository.hibernate.entity.Position;
import io.reactivestax.repository.hibernate.entity.SecuritiesReference;
import io.reactivestax.repository.hibernate.entity.TradePayload;
import io.reactivestax.service.TradeProcessorService;
import io.reactivestax.service.TradeService;
import io.reactivestax.service.TradeTestService;
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
    private TradePayloadRepository tradePayloadRepository;
    private JournalEntryRepository journalEntryRepository;
    private LookupSecuritiesRepository lookupSecuritiesRepository;
    private PositionsRepository positionsRepository;
    private ConnectionUtil<Session> connectionUtil;
    private TransactionUtil transactionUtil;
    private final TradeTestService tradeTestService = TradeTestService.getInstance();
    private final io.reactivestax.type.dto.JournalEntry journalEntryDto1 = tradeTestService.getJournalEntryDto1();
    private final io.reactivestax.type.dto.JournalEntry journalEntryDto2 = tradeTestService.getJournalEntryDto2();
    private ApplicationPropertiesUtils applicationPropertiesUtils;
    private TradeService tradeService;
    private TradeProcessorService tradeProcessorService;
    private Logger logger = Logger.getLogger(ConsumerHibernateTest.class.getName());
    private final List<TradePayload> tradePayloadList = new ArrayList<>();

    @Before
    public void setUp() {
        applicationPropertiesUtils = ApplicationPropertiesUtils.getInstance("applicationHibernateTest.properties");
        applicationPropertiesUtils.loadApplicationProperties("applicationHibernateTest.properties");
        connectionUtil = HibernateTransactionUtil.getInstance();
        tradePayloadRepository = BeanFactory.getTradePayloadRepository();
        journalEntryRepository = BeanFactory.getJournalEntryRepository();
        lookupSecuritiesRepository = BeanFactory.getLookupSecuritiesRepository();
        positionsRepository = BeanFactory.getPositionsRepository();
        transactionUtil = BeanFactory.getTransactionUtil();
        transactionUtil.startTransaction();
        tradeProcessorService = TradeProcessorService.getInstance();
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

    private <T> void cleanUpCall(CriteriaDelete<T> criteriaDelete, Session session) {
        session.createMutationQuery(criteriaDelete).executeUpdate();
    }

    @After
    public void cleanUp() {
        transactionUtil.startTransaction();
        Session session = connectionUtil.getConnection();
        CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
        CriteriaDelete<io.reactivestax.repository.hibernate.entity.TradePayload> criteriaDeleteTradePayload = criteriaBuilder.createCriteriaDelete(io.reactivestax.repository.hibernate.entity.TradePayload.class);
        cleanUpCall(criteriaDeleteTradePayload, session);
        CriteriaDelete<io.reactivestax.repository.hibernate.entity.JournalEntry> criteriaDeleteJournalEntry =
                criteriaBuilder.createCriteriaDelete(io.reactivestax.repository.hibernate.entity.JournalEntry.class);
        cleanUpCall(criteriaDeleteJournalEntry, session);
        CriteriaDelete<io.reactivestax.repository.hibernate.entity.Position> criteriaDeletePositions =
                criteriaBuilder.createCriteriaDelete(io.reactivestax.repository.hibernate.entity.Position.class);
        cleanUpCall(criteriaDeletePositions, session);
        CriteriaDelete<io.reactivestax.repository.hibernate.entity.SecuritiesReference> criteriaDeleteSecuritiesReference =
                criteriaBuilder.createCriteriaDelete(io.reactivestax.repository.hibernate.entity.SecuritiesReference.class);
        cleanUpCall(criteriaDeleteSecuritiesReference, session);
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
    public void testInsertIntoJournalEntry() {
        transactionUtil.startTransaction();
        journalEntryRepository.insertIntoJournalEntry(tradeTestService.getJournalEntryDto1()).ifPresent(id -> {
            transactionUtil.commitTransaction();
            Session session = connectionUtil.getConnection();
            JournalEntry journalEntry = session.get(JournalEntry.class, id);
            Assert.assertEquals(tradeTestService.getJournalEntryDto1().getAccountNumber(), journalEntry.getAccountNumber());
        });
    }

    @Test
    public void testUpdateJournalEntryStatus() {
        transactionUtil.startTransaction();
        journalEntryRepository.insertIntoJournalEntry(journalEntryDto1).ifPresent(id -> {
            transactionUtil.commitTransaction();
            transactionUtil.startTransaction();
            journalEntryRepository.updateJournalEntryStatus(id);
            transactionUtil.commitTransaction();
            Session session = connectionUtil.getConnection();
            JournalEntry journalEntry = session.get(JournalEntry.class, id);
            Assert.assertEquals(PostedStatus.POSTED, journalEntry.getPostedStatus());
        });
    }

    @Test
    public void testLookupSecuritiesWithExistingCusip() {
        boolean exist = lookupSecuritiesRepository.lookupSecurities("TSLA");
        Assert.assertTrue(exist);
    }

    @Test
    public void testLookupSecuritiesWithNonExistingCusip() {
        boolean exist = lookupSecuritiesRepository.lookupSecurities("V");
        Assert.assertFalse(exist);
    }

    @Test
    public void testInsertPosition() {
        transactionUtil.startTransaction();
        positionsRepository.upsertPosition(tradeTestService.getPositionDto1());
        transactionUtil.commitTransaction();
        Session session = connectionUtil.getConnection();
        Position position = session
                .createQuery("from Position where positionCompositeKey.accountNumber = :accountNumber " +
                "and positionCompositeKey.securityCusip = :securityCusip", Position.class)
                .setParameter("accountNumber", tradeTestService.getPositionDto1().getAccountNumber())
                .setParameter("securityCusip", tradeTestService.getPositionDto1().getSecurityCusip())
                .getSingleResult();
        Assert.assertNotNull(position);
    }

    @Test
    public void testUpdatePosition() {
        transactionUtil.startTransaction();
        positionsRepository.upsertPosition(tradeTestService.getPositionDto1());
        transactionUtil.commitTransaction();
        transactionUtil.startTransaction();
        positionsRepository.upsertPosition(tradeTestService.getPositionDto2());
        transactionUtil.commitTransaction();
        Session session = connectionUtil.getConnection();
        Position position = session
                .createQuery("from Position where positionCompositeKey.accountNumber = :accountNumber " +
                "and positionCompositeKey.securityCusip = :securityCusip", Position.class)
                .setParameter("accountNumber", tradeTestService.getPositionDto1().getAccountNumber())
                .setParameter("securityCusip",
                tradeTestService.getPositionDto1().getSecurityCusip())
                .getSingleResult();
        Assert.assertEquals(1, position.getVersion());
    }

    @Test
    public void testJournalEntryTransaction() {
        transactionUtil.startTransaction();
        io.reactivestax.type.dto.JournalEntry journalEntry = tradeProcessorService.journalEntryTransaction(
                "TDB_000001,2024-09-19 22:16:18,TDB_CUST_5214938,TSLA,BUY,1,638.02".split(","), 1L);
        transactionUtil.commitTransaction();
        Session session = connectionUtil.getConnection();
        JournalEntry journalEntryEntity = session.get(JournalEntry.class, journalEntry.getId());
        Assert.assertEquals("TDB_CUST_5214938", journalEntryEntity.getAccountNumber());
    }

    @Test
    public void testPositionTransaction() {
        transactionUtil.startTransaction();
//        tradeProcessorService.positionTransaction();
    }
}