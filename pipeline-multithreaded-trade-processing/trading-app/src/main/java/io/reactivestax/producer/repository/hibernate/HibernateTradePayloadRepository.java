package io.reactivestax.producer.repository.hibernate;

import io.reactivestax.producer.repository.TradePayloadRepository;
import io.reactivestax.producer.type.dto.TradePayload;
import io.reactivestax.producer.type.enums.ValidityStatus;
import io.reactivestax.producer.util.database.hibernate.HibernateTransactionUtil;
import org.hibernate.Session;

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
        Session session = HibernateTransactionUtil.getInstance().getConnection();
        io.reactivestax.producer.repository.hibernate.entity.TradePayload tradePayloadEntity =
                prepareTradePayloadEntity(tradePayload);
        session.persist(tradePayloadEntity);
    }

    private io.reactivestax.producer.repository.hibernate.entity.TradePayload prepareTradePayloadEntity(TradePayload tradePayload) {
        io.reactivestax.producer.repository.hibernate.entity.TradePayload tradePayloadEntity = new io.reactivestax.producer.repository.hibernate.entity.TradePayload();
        tradePayloadEntity.setTradeNumber(tradePayload.getTradeNumber());
        tradePayloadEntity.setPayload(tradePayload.getPayload());
        tradePayloadEntity.setValidityStatus(ValidityStatus.valueOf(tradePayload.getValidityStatus()));
        return tradePayloadEntity;
    }
}
