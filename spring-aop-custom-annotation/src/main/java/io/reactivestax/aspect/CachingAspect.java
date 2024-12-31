package io.reactivestax.aspect;

public interface CachingAspect {
    void storeAndRetrieveFromCache();

    void clearCache();
}
