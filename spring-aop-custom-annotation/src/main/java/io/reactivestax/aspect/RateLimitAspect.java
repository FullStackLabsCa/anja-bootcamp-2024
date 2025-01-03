package io.reactivestax.aspect;

import io.reactivestax.customannotation.RateLimit;
import io.reactivestax.enums.RateLimitAlgorithm;
import io.reactivestax.service.FixedWindowService;
import io.reactivestax.service.TokenBucketService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Order(3)
public class RateLimitAspect {
    private final Log log = LogFactory.getLog(RateLimitAspect.class);

    private io.reactivestax.service.RateLimitAlgorithm rateLimitAlgorithm;
    private final FixedWindowService fixedWindowService;
    private final TokenBucketService tokenBucketService;

    @Autowired
    public RateLimitAspect(FixedWindowService fixedWindowService,
//                           SlidingWindowService slidingWindowService,
            TokenBucketService tokenBucketService
    ) {
        this.fixedWindowService = fixedWindowService;
//        this.slidingWindowService = slidingWindowService;
        this.tokenBucketService = tokenBucketService;
    }

    @Around("@annotation(io.reactivestax.customannotation.RateLimit)")
    public void imposeRateLimit(ProceedingJoinPoint joinPoint) throws Throwable {
        RateLimit annotation = ((MethodSignature) joinPoint.getSignature()).getMethod().getAnnotation(RateLimit.class);
        RateLimitAlgorithm algorithm = annotation.algorithm();
        int limit = annotation.limit();
        long timeFrame = annotation.timeFrame();
        log.debug("Algorithm: " + algorithm);
        log.debug("Limit: " + limit);
        log.debug("TimeFrame: " + timeFrame);
        switch (algorithm) {
            case FIXED_WINDOW:
                fixedWindowService.imposeAlgorithm(joinPoint, limit, timeFrame);
                break;
            case TOKEN_BUCKET:
                rateLimitAlgorithm = tokenBucketService;
                break;
            case SLIDING_WINDOW:
//                rateLimitAlgorithm = slidingWindowService;
                break;
        }
//        rateLimitAlgorithm.imposeAlgorithm(joinPoint, limit, timeFrame);
        joinPoint.proceed();
    }
}
