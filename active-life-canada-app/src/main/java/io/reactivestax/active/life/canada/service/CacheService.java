package io.reactivestax.active.life.canada.service;

import io.reactivestax.active.life.canada.constant.ExceptionHandlerConst;
import io.reactivestax.active.life.canada.dto.CartDto;
import io.reactivestax.active.life.canada.exception.InvalidRequestException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class CacheService {

    private final CacheManager cacheManager;

    @Value("${spring.cache.cache-names}")
    private String cacheName;

    public List<CartDto> getCart(String loggedInMemberId) {
        Cache cache = cacheManager.getCache(cacheName);
        List<CartDto> cart = new ArrayList<>();
        if (cache != null) {
            cart = cache.get(loggedInMemberId, List.class);
            if (cart == null) cart = new ArrayList<>();
        }
        return cart;
    }

    public List<CartDto> addToCache(String loggedInMemberId, CartDto cartDto) {
        Cache cache = cacheManager.getCache(cacheName);
        List<CartDto> cartDtoList = getCart(loggedInMemberId);
        if (!cartDtoList.contains(cartDto) && cache != null) {
            cartDtoList.add(cartDto);
            cache.put(loggedInMemberId, cartDtoList);
            log.info("Added to cart: {}", cartDto);
        } else throw new InvalidRequestException(ExceptionHandlerConst.ADD_TO_CART_FAILED_ALREADY_IN_CART);

        return cartDtoList;
    }

    public void clearCart(String loggedInMemberId) {
        Cache cache = cacheManager.getCache(cacheName);
        if (cache != null) {
            cache.evict(loggedInMemberId);
        }
    }
}
