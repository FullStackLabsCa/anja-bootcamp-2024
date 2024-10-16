package io.reactivestax.repository.hibernate;

import io.reactivestax.factory.BeanFactory;
import io.reactivestax.repository.TradePayloadRepository;
import io.reactivestax.utility.database.TransactionUtil;
import org.hibernate.Session;

import io.reactivestax.entity.TradePayload;
import io.reactivestax.enums.LookupStatusEnum;
import io.reactivestax.enums.PostedStatusEnum;

public class HibernateTradePayloadRepository implements TradePayloadRepository {
    private static HibernateTradePayloadRepository instance;

    private HibernateTradePayloadRepository() {
    }

    public static synchronized HibernateTradePayloadRepository getInstance() {
        if (instance == null) {
            instance = new HibernateTradePayloadRepository();
        }
        return instance;
    }

    @Override
    public void insertTradeRawPayload(TradePayload tradePayload) {
        Session session = getSession();
        session.persist(tradePayload);
    }

    @Override
    public TradePayload readRawPayload(String tradeNumber) {
        Session session = getSession();
        return session.createQuery("from TradePayload tp where tp.tradeNumber = :tradeNumber", TradePayload.class)
                .setParameter("tradeNumber", tradeNumber)
                .getSingleResult();
    }

    @Override
    public void updateTradePayloadLookupStatus(boolean lookupStatus, int tradeId) {
        Session session = getSession();
        TradePayload tradePayload = session.get(TradePayload.class, tradeId);
        tradePayload.setLookupStatus(lookupStatus ? LookupStatusEnum.PASS : LookupStatusEnum.FAIL);
    }

    private static Session getSession() {
        TransactionUtil transactionUtil = BeanFactory.getTransactionUtil();
        Session session = (Session)transactionUtil.getConnection();
        return session;
    }

    @Override
    public void updateTradePayloadPostedStatus(int tradeId) {
        Session session = getSession();
        TradePayload tradePayload = session.get(TradePayload.class, tradeId);
        tradePayload.setJournalEntryStatus(PostedStatusEnum.POSTED);
    }
}
