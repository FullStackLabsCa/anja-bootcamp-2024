package io.reactivestax.service;

import io.reactivestax.customannotation.*;
import io.reactivestax.enums.RateLimitAlgorithm;
import io.reactivestax.model.Trade;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Service;

@Service
public class CacheServiceImpl {
    private final Log log = LogFactory.getLog(CacheServiceImpl.class);

    @RateLimit(algorithm = RateLimitAlgorithm.TOKEN_BUCKET, limit = 4, timeFrame = 500)
    @Timer
    @Synchronized
    @Cache
    public Trade processTrade(String tradeNo, String cusip) {
        log.debug("Executing processTrade method.");
        return new Trade(tradeNo, cusip);
    }

    @CacheEvict
    public Trade getObject(String tradeNo, String cusip) {
        log.debug("Executing getObject method.");
        return new Trade(tradeNo, cusip);
    }
}
