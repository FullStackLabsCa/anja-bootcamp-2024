package io.reactivestax.repository;

import org.hibernate.Session;

import io.reactivestax.entity.TradePayload;
import io.reactivestax.enums.LookupStatusEnum;
import io.reactivestax.enums.PostedStatusEnum;
import io.reactivestax.utility.HibernateServiceUtil;

public class TradePayloadRepository implements ReadAndWriteTradePayload {
    @Override
    public void insertTradeRawPayload(TradePayload tradePayload, Session session) {
        HibernateServiceUtil.getSession();
        session.beginTransaction();
        session.persist(tradePayload);
        session.getTransaction().commit();
        session.clear();
    }

    @Override
    public TradePayload readRawPayload(String tradeNumber, Session session) {
        return session.createQuery("from TradePayload tp where tp.tradeNumber = :tradeNumber", TradePayload.class)
                .setParameter("tradeNumber", tradeNumber)
                .getSingleResult();
    }

    @Override
    public void updateTradePayloadLookupStatus(boolean lookupStatus, int tradeId, Session session) {
        TradePayload tradePayload = session.get(TradePayload.class, tradeId);
        tradePayload.setLookupStatus(lookupStatus ? LookupStatusEnum.PASS : LookupStatusEnum.FAIL);
    }

    @Override
    public void updateTradePayloadPostedStatus(int tradeId, Session session) {
        TradePayload tradePayload = session.get(TradePayload.class, tradeId);
        tradePayload.setJournalEntryStatus(PostedStatusEnum.POSTED);
    }
}
