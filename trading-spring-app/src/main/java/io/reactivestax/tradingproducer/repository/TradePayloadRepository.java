package io.reactivestax.tradingproducer.repository;


import io.reactivestax.tradingproducer.type.entity.TradePayload;

public interface TradePayloadRepository {
    void insertTradeRawPayload(TradePayload tradePayload);
}
