package io.reactivestax.repository;

import java.util.Optional;

import io.reactivestax.type.dto.TradePayloadDTO;

public interface TradePayloadRepository {
    Optional<TradePayloadDTO> readRawPayload(String tradeNumber);

    void updateTradePayloadLookupStatus(boolean lookupStatus, Long tradeId);

    void updateTradePayloadPostedStatus(Long tradeId);
}
