package io.reactivestax.aspect;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Aspect
@Order(1)
public class TimerAspect {
    private final Log log = LogFactory.getLog(TimerAspect.class);

    @Around("@annotation(io.reactivestax.customannotation.Timer)")
    public void calculateExecutionTime(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        log.debug("Started timer");
        long startTime = System.currentTimeMillis();
        proceedingJoinPoint.proceed();
        long endTime = System.currentTimeMillis();
        long executionTime = endTime - startTime;
        log.debug("Execution time: " + executionTime + "ms");
    }
}
