package io.reactivestax.repository;

import io.reactivestax.type.entity.TradePayload;

public interface TradePayloadRepository {
    TradePayload readRawPayload(String tradeNumber);

    void updateTradePayloadLookupStatus(boolean lookupStatus, Long tradeId);

    void updateTradePayloadPostedStatus(Long tradeId);
}
