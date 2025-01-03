package io.reactivestax.service;

import io.reactivestax.exception.NoAvailableRequestsException;
import io.reactivestax.model.FixedWindow;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class FixedWindowService implements RateLimitAlgorithm  {
    private final Log log = LogFactory.getLog(FixedWindowService.class);
    private final Map<String, FixedWindow> limitMap = new ConcurrentHashMap<>();

    @Override
    public void imposeAlgorithm(ProceedingJoinPoint joinPoint, int limit, long timeFrame) {
        String methodSignature = ((MethodSignature) joinPoint.getSignature()).getMethod().toString();
        FixedWindow window = limitMap.getOrDefault(methodSignature, new FixedWindow(limit, System.currentTimeMillis()));
        if (System.currentTimeMillis() - window.getStartTime() >= timeFrame) {
            window.setStartTime(System.currentTimeMillis());
            window.setRemainingRequests(limit);
        }
        int remainingRequests = window.getRemainingRequests();
        window.setRemainingRequests(remainingRequests - 1);
        limitMap.put(methodSignature, window);
        if (remainingRequests != 0) {
            log.debug("Remaining requests count: " + (remainingRequests - 1));
        } else throw new NoAvailableRequestsException();
    }
}
