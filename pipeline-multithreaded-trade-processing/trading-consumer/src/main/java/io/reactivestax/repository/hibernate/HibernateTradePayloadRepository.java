package io.reactivestax.repository.hibernate;

import io.reactivestax.repository.TradePayloadRepository;
import io.reactivestax.repository.hibernate.entity.TradePayload;
import io.reactivestax.type.enums.LookupStatus;
import io.reactivestax.type.enums.PostedStatus;
import io.reactivestax.util.database.hibernate.HibernateTransactionUtil;
import org.hibernate.Session;

import java.util.Optional;

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
    public Optional<io.reactivestax.type.dto.TradePayload> readRawPayload(String tradeNumber) {
        Session session = HibernateTransactionUtil.getInstance().getConnection();
        Optional<io.reactivestax.repository.hibernate.entity.TradePayload> optionalTradePayload = session
                .createQuery("from TradePayload tp where tp.tradeNumber = :tradeNumber"
                        , io.reactivestax.repository.hibernate.entity.TradePayload.class)
                .setParameter("tradeNumber", tradeNumber)
                .stream()
                .findFirst();

        return getTradePayloadDTO(optionalTradePayload);
    }

    @Override
    public void updateTradePayloadLookupStatus(boolean lookupStatus, Long tradeId) {
        Session session = HibernateTransactionUtil.getInstance().getConnection();
        TradePayload tradePayload = session.get(TradePayload.class, tradeId);
        tradePayload.setLookupStatus(lookupStatus ? LookupStatus.PASS : LookupStatus.FAIL);
    }

    @Override
    public void updateTradePayloadPostedStatus(Long tradeId) {
        Session session = HibernateTransactionUtil.getInstance().getConnection();
        TradePayload tradePayload = session.get(TradePayload.class, tradeId);
        tradePayload.setJournalEntryStatus(PostedStatus.POSTED);
    }

    private Optional<io.reactivestax.type.dto.TradePayload>
                     getTradePayloadDTO(Optional<TradePayload> optionalTradePayloadEntity) {
        return optionalTradePayloadEntity.map(tradePayloadEntity -> {
            io.reactivestax.type.dto.TradePayload tradePayload = new io.reactivestax.type.dto.TradePayload();
            tradePayload.setId(tradePayloadEntity.getId());
            tradePayload.setTradeNumber(tradePayloadEntity.getTradeNumber());
            tradePayload.setPayload(tradePayloadEntity.getPayload());
            tradePayload.setLookupStatus(String.valueOf(tradePayloadEntity.getLookupStatus()));
            tradePayload.setValidityStatus(String.valueOf(tradePayloadEntity.getValidityStatus()));
            tradePayload.setJournalEntryStatus(String.valueOf(tradePayloadEntity.getJournalEntryStatus()));
            return tradePayload;
        });
    }
}
