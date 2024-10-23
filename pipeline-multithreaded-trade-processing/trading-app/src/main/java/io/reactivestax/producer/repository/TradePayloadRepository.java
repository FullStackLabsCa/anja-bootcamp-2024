package io.reactivestax.producer.repository;

import io.reactivestax.producer.type.entity.TradePayload;

public interface TradePayloadRepository {
    void insertTradeRawPayload(TradePayload tradePayload);
}
