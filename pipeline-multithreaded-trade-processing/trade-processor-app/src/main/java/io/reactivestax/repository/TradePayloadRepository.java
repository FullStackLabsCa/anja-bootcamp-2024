package io.reactivestax.repository;

import io.reactivestax.entity.TradePayload;
import org.hibernate.Session;

public interface TradePayloadRepository {
    void insertTradeRawPayload(TradePayload tradePayload, Session session);

    TradePayload readRawPayload(String tradeId, Session session);

    void updateTradePayloadLookupStatus(boolean lookupStatus, int tradeId, Session session);

    void updateTradePayloadPostedStatus(int tradeId, Session session);
}
