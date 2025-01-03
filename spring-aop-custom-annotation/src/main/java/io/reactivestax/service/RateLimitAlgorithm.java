package io.reactivestax.service;

import org.aspectj.lang.ProceedingJoinPoint;

public interface RateLimitAlgorithm {
    void imposeAlgorithm(ProceedingJoinPoint joinPoint, int limit, long timeFrame);
}
