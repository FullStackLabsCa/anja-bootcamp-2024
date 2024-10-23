package io.reactivestax.repository;


import io.reactivestax.type.dto.TradePayload;

public interface TradePayloadRepository {
    void insertTradeRawPayload(TradePayload tradePayload);
}
