package io.reactivestax.service;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class SlidingWindowService implements RateLimitAlgorithm {
    private final Log log = LogFactory.getLog(SlidingWindowService.class);
    private final Map<String, List<Long>> methodStartTimeMap = new ConcurrentHashMap<>();

    @Override
    public void imposeAlgorithm(ProceedingJoinPoint joinPoint, int limit, long timeFrame)  {
        String methodSignature = ((MethodSignature) joinPoint.getSignature()).getMethod().toString();
        List<Long> startTimeList = methodStartTimeMap.getOrDefault(methodSignature, new ArrayList<>());
//        startTimeList.forEach(startTime -> {
//            if (System.currentTimeMillis() - startTime >= timeFrame) startTimeList.remove(startTime);
//        });
//        if (startTimeList.size() < limit) {
//            startTimeList.add(System.currentTimeMillis());
//            methodStartTimeMap.put(methodSignature, startTimeList);
//            log.debug("Served the request using sliding window algorithm");
//            log.debug("Remaining requests: " + (limit - startTimeList.size()));
//        } else throw new NoAvailableRequestsException();
//        joinPoint.proceed();
    }
}
