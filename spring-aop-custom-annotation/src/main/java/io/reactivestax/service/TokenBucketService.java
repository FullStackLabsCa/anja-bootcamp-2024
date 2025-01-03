package io.reactivestax.service;

import org.aspectj.lang.ProceedingJoinPoint;
import org.springframework.stereotype.Service;

@Service
public class TokenBucketService implements RateLimitAlgorithm {
    @Override
    public void imposeAlgorithm(ProceedingJoinPoint joinPoint, int limit, long timeFrame) {

    }
}
