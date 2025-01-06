package io.reactivestax.service;

import io.reactivestax.customannotation.*;
import io.reactivestax.enums.RateLimitAlgorithm;
import io.reactivestax.exception.InvalidParamsException;
import io.reactivestax.model.Trade;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Service;

@Service("cacheService")
public class CacheService implements CacheProcessor {
    private final Log log = LogFactory.getLog(CacheService.class);

    @RateLimit(algorithm = RateLimitAlgorithm.TOKEN_BUCKET, limit = 4, timeFrame = 500)
    @Timer
    @Synchronized
    @Cache
    @Override
    public Trade processTrade(String tradeNo, String cusip) {
        log.debug("Executing processTrade method.");
        return new Trade(tradeNo, cusip);
    }

    @CacheEvict
    @Override
    public Trade getObject(String tradeNo, String cusip) {
        log.debug("Executing getObject method.");
        return new Trade(tradeNo, cusip);
    }

    @Override
    @Retry(retryCount = 4, retryInterval = 500)
    public void validateParams(String tradeNo) {
        throw new InvalidParamsException("Invalid trade number.");
    }
}
