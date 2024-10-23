package io.reactivestax.producer.repository;

import io.reactivestax.producer.util.database.hibernate.entity.TradePayload;

public interface TradePayloadRepository {
    void insertTradeRawPayload(TradePayload tradePayload);
}
