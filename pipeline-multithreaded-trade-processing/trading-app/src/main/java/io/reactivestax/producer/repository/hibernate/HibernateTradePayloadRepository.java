package io.reactivestax.producer.repository.hibernate;

import io.reactivestax.producer.repository.TradePayloadRepository;
import io.reactivestax.producer.type.entity.TradePayload;
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
        session.persist(tradePayload);
    }
}
