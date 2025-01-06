package io.reactivestax.service;

import io.reactivestax.exception.NoAvailableRequestsException;
import io.reactivestax.model.TokenBucket;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class TokenBucketService implements RateLimitAlgorithm {
    private final Log log = LogFactory.getLog(TokenBucketService.class);
    private final Map<String, TokenBucket> tokenBucketMap = new ConcurrentHashMap<>();

    @Override
    public void imposeAlgorithm(ProceedingJoinPoint joinPoint, int limit, long timeFrame) {
        String methodSignature = ((MethodSignature) joinPoint.getSignature()).getMethod().toString();
        TokenBucket tokenBucket = new TokenBucket();
        if (tokenBucketMap.containsKey(methodSignature)) {
            tokenBucket = tokenBucketMap.get(methodSignature);
        } else {
            tokenBucket.setRemainingRequests(limit);
            tokenBucket.setLastRemovalTime(System.currentTimeMillis());
        }

        int remainingRequests = tokenBucket.getRemainingRequests();
        int removal = (int) (System.currentTimeMillis() - tokenBucket.getLastRemovalTime());
        int count = (int) (removal / timeFrame);
        while (count != 0) {
            if (remainingRequests != limit) {
                remainingRequests++;
                tokenBucket.setLastRemovalTime(System.currentTimeMillis());
                log.debug("Added one more request.");
            } else {
                tokenBucket.setLastRemovalTime(System.currentTimeMillis());
                break;
            }
            count--;
        }
        if (remainingRequests != 0) {
            tokenBucket.setRemainingRequests(remainingRequests - 1);
            tokenBucketMap.put(methodSignature, tokenBucket);
            log.debug("Served the request using token bucket algorithm");
            log.debug("Remaining requests: " + (remainingRequests-1));
        } else throw new NoAvailableRequestsException();
    }
}
