package io.reactivestax.repository;

import io.reactivestax.type.dto.TradePayload;

public interface TradePayloadRepository {
    TradePayload readRawPayload(String tradeNumber);

    void updateTradePayloadLookupStatus(boolean lookupStatus, Long tradeId);

    void updateTradePayloadPostedStatus(Long tradeId);
}
