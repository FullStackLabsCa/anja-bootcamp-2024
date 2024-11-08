package io.reactivestax.repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

import org.hibernate.Session;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import io.reactivestax.repository.hibernate.entity.JournalEntry;
import io.reactivestax.repository.hibernate.entity.Position;
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

public class ConsumerHibernateTest {
    TradePayloadRepository tradePayloadRepository;
    JournalEntryRepository journalEntryRepository;
    LookupSecuritiesRepository lookupSecuritiesRepository;
    PositionsRepository positionsRepository;
    ConnectionUtil<Session> connectionUtil;
    TransactionUtil transactionUtil;
    io.reactivestax.type.dto.JournalEntryDTO journalEntryDto1 = new io.reactivestax.type.dto.JournalEntryDTO();
    io.reactivestax.type.dto.JournalEntryDTO journalEntryDto2 = new io.reactivestax.type.dto.JournalEntryDTO();
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
        lookupSecuritiesRepository = BeanFactory.getLookupSecuritiesRepository();
        positionsRepository = BeanFactory.getPositionsRepository();
        transactionUtil = BeanFactory.getTransactionUtil();
        journalEntryDto1.setTradeId("TDB_000001");
        journalEntryDto1.setAccountNumber("TDB_CUST_5214938");
        journalEntryDto1.setSecurityCusip("TSLA");
        journalEntryDto1.setQuantity(1);
        journalEntryDto1.setDirection(Direction.BUY.name());
        journalEntryDto1.setTransactionTimestamp("2024-09-19 22:16:18");
        journalEntryDto2.setTradeId("TDB_000002");
        journalEntryDto2.setAccountNumber("TDB_CUST_5214938");
        journalEntryDto2.setSecurityCusip("TSLA");
        journalEntryDto2.setQuantity(1);
        journalEntryDto2.setDirection(Direction.BUY.name());
        journalEntryDto2.setTransactionTimestamp("2024-09-19 22:16:18");
        transactionUtil.startTransaction();
        String[] cusipArray = { "AAPL", "GOOGL", "AMZN", "MSFT", "TSLA", "NFLX", "FB", "NVDA", "JPM", "VISA", "MA",
                "BAC", "DIS", "INTC", "CSCO", "ORCL", "WMT", "T", "VZ", "ADBE", "CRM", "PYPL", "PFE", "XOM", "UNH" };
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
        CriteriaDelete<io.reactivestax.repository.hibernate.entity.TradePayload> criteriaDeleteTradePayload = criteriaBuilder
                .createCriteriaDelete(io.reactivestax.repository.hibernate.entity.TradePayload.class);
        session.createMutationQuery(criteriaDeleteTradePayload).executeUpdate();
        CriteriaDelete<io.reactivestax.repository.hibernate.entity.JournalEntry> criteriaDeleteJournalEntry = criteriaBuilder
                .createCriteriaDelete(io.reactivestax.repository.hibernate.entity.JournalEntry.class);
        session.createMutationQuery(criteriaDeleteJournalEntry).executeUpdate();
        CriteriaDelete<io.reactivestax.repository.hibernate.entity.Position> criteriaDeletePositions = criteriaBuilder
                .createCriteriaDelete(io.reactivestax.repository.hibernate.entity.Position.class);
        session.createMutationQuery(criteriaDeletePositions).executeUpdate();
        CriteriaDelete<io.reactivestax.repository.hibernate.entity.SecuritiesReference> criteriaDeleteSecuritiesReference = criteriaBuilder
                .createCriteriaDelete(io.reactivestax.repository.hibernate.entity.SecuritiesReference.class);
        session.createMutationQuery(criteriaDeleteSecuritiesReference).executeUpdate();
        transactionUtil.commitTransaction();
    }

    @Test
    public void testReadRawPayloadWithValidTradeNumber() {
        Optional<io.reactivestax.type.dto.TradePayloadDTO> optionalTradePayloadDto = tradePayloadRepository
                .readRawPayload("TDB_000001");
        Assert.assertEquals("TDB_000001,2024-09-19 22:16:18,TDB_CUST_5214938,TSLA,BUY,1,638.02",
                optionalTradePayloadDto.map(tradePayloadDto -> tradePayloadDto.getPayload()));
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
        io.reactivestax.type.dto.JournalEntryDTO journalEntryDto = new io.reactivestax.type.dto.JournalEntryDTO();
        journalEntryDto.setTradeId("TDB_000001");
        journalEntryDto.setAccountNumber("TDB_CUST_5214938");
        journalEntryDto.setSecurityCusip("TSLA");
        journalEntryDto.setQuantity(1);
        journalEntryDto.setDirection(Direction.BUY.name());
        journalEntryDto.setTransactionTimestamp("2024-09-19 22:16:18");
        transactionUtil.startTransaction();
        Optional<Long> entryId = journalEntryRepository.saveJournalEntry(journalEntryDto);
        transactionUtil.commitTransaction();
        Session session = connectionUtil.getConnection();
        JournalEntry journalEntry = session.get(JournalEntry.class, entryId.get());
        Assert.assertEquals(journalEntryDto.getAccountNumber(), journalEntry.getAccountNumber());
    }

    @Test
    public void testUpdateJournalEntryStatus() {
        transactionUtil.startTransaction();
        Optional<Long> entryId = journalEntryRepository.saveJournalEntry(journalEntryDto1);
        transactionUtil.commitTransaction();
        transactionUtil.startTransaction();
        journalEntryRepository.updateJournalEntryStatus(entryId.get());
        transactionUtil.commitTransaction();
        Session session = connectionUtil.getConnection();
        JournalEntry journalEntry = session.get(JournalEntry.class, entryId);
        Assert.assertEquals(PostedStatus.POSTED, journalEntry.getPostedStatus());
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
        io.reactivestax.type.dto.PositionDTO positionDto = new io.reactivestax.type.dto.PositionDTO();
        positionDto.setAccountNumber(journalEntryDto1.getAccountNumber());
        positionDto.setSecurityCusip(journalEntryDto1.getSecurityCusip());
        positionDto.setHolding((long) journalEntryDto1.getQuantity());
        transactionUtil.startTransaction();
        positionsRepository.upsertPosition(positionDto);
        transactionUtil.commitTransaction();
        Session session = connectionUtil.getConnection();
        Position position = session
                .createQuery("from Position where positionCompositeKey.accountNumber = :accountNumber " +
                        "and positionCompositeKey.securityCusip = :securityCusip", Position.class)
                .setParameter("accountNumber",
                        positionDto.getAccountNumber())
                .setParameter("securityCusip", positionDto.getSecurityCusip()).getSingleResult();
        Assert.assertNotNull(position);
    }

    @Test
    public void testUpdatePosition() {
        io.reactivestax.type.dto.PositionDTO positionDto1 = new io.reactivestax.type.dto.PositionDTO();
        positionDto1.setAccountNumber(journalEntryDto1.getAccountNumber());
        positionDto1.setSecurityCusip(journalEntryDto1.getSecurityCusip());
        positionDto1.setHolding((long) journalEntryDto1.getQuantity());
        transactionUtil.startTransaction();
        positionsRepository.upsertPosition(positionDto1);
        transactionUtil.commitTransaction();
        io.reactivestax.type.dto.PositionDTO positionDto2 = new io.reactivestax.type.dto.PositionDTO();
        positionDto2.setAccountNumber(journalEntryDto2.getAccountNumber());
        positionDto2.setSecurityCusip(journalEntryDto2.getSecurityCusip());
        positionDto2.setHolding((long) journalEntryDto2.getQuantity());
        transactionUtil.startTransaction();
        positionsRepository.upsertPosition(positionDto2);
        transactionUtil.commitTransaction();
        Session session = connectionUtil.getConnection();
        Position position = session
                .createQuery("from Position where positionCompositeKey.accountNumber = :accountNumber " +
                        "and positionCompositeKey.securityCusip = :securityCusip", Position.class)
                .setParameter("accountNumber",
                        positionDto1.getAccountNumber())
                .setParameter("securityCusip", positionDto1.getSecurityCusip()).getSingleResult();
        Assert.assertEquals(1, position.getVersion());
    }
}