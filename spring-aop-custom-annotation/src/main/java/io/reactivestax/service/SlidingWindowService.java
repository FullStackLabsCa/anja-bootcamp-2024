package io.reactivestax.service;

import io.reactivestax.exception.NoAvailableRequestsException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

@Service
public class SlidingWindowService implements RateLimitAlgorithm {
    private final Log log = LogFactory.getLog(SlidingWindowService.class);
    private final Map<String, CopyOnWriteArrayList<Long>> methodStartTimeMap = new ConcurrentHashMap<>();

    @Override
    public void imposeAlgorithm(ProceedingJoinPoint joinPoint, int limit, long timeFrame) {
        String methodSignature = ((MethodSignature) joinPoint.getSignature()).getMethod().toString();
        CopyOnWriteArrayList<Long> startTimeList = methodStartTimeMap.getOrDefault(methodSignature, new CopyOnWriteArrayList<>());
        for (Long startTime : startTimeList) {
            long startTime1 = System.currentTimeMillis() - startTime;
            if (startTime1 >= timeFrame) {
                startTimeList.remove(startTime);
                log.debug("Window slided.");
            }
        }
        if (startTimeList.size() < limit) {
            startTimeList.add(System.currentTimeMillis());
            methodStartTimeMap.put(methodSignature, startTimeList);
            log.debug("Served the request using sliding window algorithm");
            log.debug("Remaining requests: " + (limit - startTimeList.size()));
        } else throw new NoAvailableRequestsException();
    }
}
