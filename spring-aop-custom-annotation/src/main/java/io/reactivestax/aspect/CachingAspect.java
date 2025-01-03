package io.reactivestax.aspect;

import io.reactivestax.model.Trade;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Aspect
@Component
@Order(4)
public class CachingAspect {
    private final Log log = LogFactory.getLog(CachingAspect.class);
    private final Map<String, Trade> cache = new ConcurrentHashMap<>();

    @Around("execution(@io.reactivestax.customannotation.Cache * *(..))")
    public Trade storeOrRetrieveFromCache(ProceedingJoinPoint joinPoint) throws Throwable {
        String args = Arrays.toString(joinPoint.getArgs());
        if (cache.containsKey(args)) {
            log.debug("Found the object in cache.");
            return cache.get(args);
        }
        log.debug("Couldn't find the object in cache.");
        Trade result = (Trade) joinPoint.proceed();
        cache.put(args, result);
        return result;
    }

    @Around("@annotation(io.reactivestax.customannotation.CacheEvict)")
    public Trade evictValueFromCache(ProceedingJoinPoint joinPoint) throws Throwable {
        String args = Arrays.toString(joinPoint.getArgs());
        if (cache.containsKey(args)) {
            log.debug("Found the object in cache, removing it.");
            return cache.remove(args);
        }
        log.debug("Couldn't find the object in cache.");
        return (Trade) joinPoint.proceed();
    }
}
