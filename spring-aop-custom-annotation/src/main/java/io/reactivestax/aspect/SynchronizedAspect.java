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
public class SynchronizedAspect {
    private final Log log = LogFactory.getLog(SynchronizedAspect.class);

    @Around("execution(@io.reactivestax.customannotation.Synchronized * *(..))")
    public void synchronizeExecution(ProceedingJoinPoint joinPoint) throws Throwable {
        synchronized (this) {
            log.debug("---------------------------------------->>>>>>>>>>>>>>>..executing synchronized");
            joinPoint.proceed();
        }
    }
}
