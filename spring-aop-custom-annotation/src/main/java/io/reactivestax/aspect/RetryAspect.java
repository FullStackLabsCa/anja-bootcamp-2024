package io.reactivestax.aspect;

import io.reactivestax.customannotation.Retry;
import io.reactivestax.exception.MaxRetryCountReachedException;
import lombok.extern.log4j.Log4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Log4j
@Component
@Aspect
public class RetryAspect {
    private final Map<String, Integer> paramRetryCountMap = new ConcurrentHashMap<>();

    @Around("@annotation(io.reactivestax.customannotation.Retry)")
    void retry(ProceedingJoinPoint joinPoint) throws Throwable {
        try {
            joinPoint.proceed();
        } catch (RuntimeException e) {
            Method method = ((MethodSignature) joinPoint.getSignature()).getMethod();
            String methodName = method.toString();
            Retry annotation = ((MethodSignature) joinPoint.getSignature()).getMethod().getAnnotation(Retry.class);
            Integer paramRetryCount = paramRetryCountMap.getOrDefault(methodName, annotation.retryCount());
            paramRetryCount--;
            paramRetryCountMap.put(methodName, paramRetryCount);
            log.debug("Waiting....");
            Thread.sleep(annotation.retryInterval());
            log.debug("Retrying.....");
            try {
                joinPoint.proceed();
            } catch (RuntimeException ex) {
                if (paramRetryCount == 0) throw new MaxRetryCountReachedException("Exceeded max retry count limit.");
            }
        }
    }
}
