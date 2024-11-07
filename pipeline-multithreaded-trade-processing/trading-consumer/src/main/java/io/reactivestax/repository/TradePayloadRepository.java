package io.reactivestax.repository;

import io.reactivestax.type.dto.TradePayload;

import java.util.Optional;

public interface TradePayloadRepository {
    Optional<TradePayload> readRawPayload(String tradeNumber);

    void updateTradePayloadLookupStatus(boolean lookupStatus, Long tradeId);

    void updateTradePayloadPostedStatus(Long tradeId);
}
