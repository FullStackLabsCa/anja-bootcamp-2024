package io.reactivestax.consumer.repository;

import io.reactivestax.consumer.type.entity.TradePayload;

public interface TradePayloadRepository {
    TradePayload readRawPayload(String tradeNumber);

    void updateTradePayloadLookupStatus(boolean lookupStatus, Long tradeId);

    void updateTradePayloadPostedStatus(Long tradeId);
}
