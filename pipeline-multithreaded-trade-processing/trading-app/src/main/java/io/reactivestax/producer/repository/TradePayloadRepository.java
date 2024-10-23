package io.reactivestax.producer.repository;


import io.reactivestax.producer.type.dto.TradePayload;

public interface TradePayloadRepository {
    void insertTradeRawPayload(TradePayload tradePayload);
}
