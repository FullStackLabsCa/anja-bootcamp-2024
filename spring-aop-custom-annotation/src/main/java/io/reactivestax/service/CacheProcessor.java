package io.reactivestax.service;

import io.reactivestax.model.Trade;

public interface CacheProcessor {
    Trade processTrade(String tradeNo, String cusip);

    Trade getObject(String tradeNo, String cusip);

    void validateParams(String tradeNo);
}
