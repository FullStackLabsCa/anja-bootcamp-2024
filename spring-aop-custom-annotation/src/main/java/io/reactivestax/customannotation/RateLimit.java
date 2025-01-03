package io.reactivestax.customannotation;

import io.reactivestax.enums.RateLimitAlgorithm;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RateLimit {
    RateLimitAlgorithm algorithm() default RateLimitAlgorithm.FIXED_WINDOW;

    int limit() default 10;

    long timeFrame() default 70000;
}
