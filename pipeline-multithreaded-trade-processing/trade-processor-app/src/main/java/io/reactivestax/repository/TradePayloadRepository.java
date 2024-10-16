package io.reactivestax.repository;

import io.reactivestax.entity.TradePayload;
import org.hibernate.Session;

public interface TradePayloadRepository {
    void insertTradeRawPayload(TradePayload tradePayload);

    TradePayload readRawPayload(String tradeNumber);

    void updateTradePayloadLookupStatus(boolean lookupStatus, int tradeId);

    void updateTradePayloadPostedStatus(int tradeId);
}
