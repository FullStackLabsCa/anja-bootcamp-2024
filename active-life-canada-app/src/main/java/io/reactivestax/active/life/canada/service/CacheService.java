package io.reactivestax.active.life.canada.service;

import io.reactivestax.active.life.canada.entity.FamilyCourseRegistration;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CacheService {

    private final CacheManager cacheManager;

    @Value("${spring.application.cacheName}")
    private final String cacheName;

    @Cacheable(value = "${spring.application.cacheName}", key = "#loggedInMemberId")
    public List<FamilyCourseRegistration> getCart(String loggedInMemberId) {
        return new ArrayList<>();
    }

    @CachePut(value = "${spring.application.cacheName}", key = "#loggedInMemberId")
    public List<FamilyCourseRegistration> addToCache(String loggedInMemberId, FamilyCourseRegistration familyCourseRegistration) {
        Cache cache = cacheManager.getCache(cacheName);
        List<FamilyCourseRegistration> familyCourseRegistrationList = new ArrayList<>();

        if (cache != null) {
            familyCourseRegistrationList = cache.get(loggedInMemberId, List.class);
        }

        assert familyCourseRegistrationList != null;
        familyCourseRegistrationList.add(familyCourseRegistration);

        return familyCourseRegistrationList;
    }
}
