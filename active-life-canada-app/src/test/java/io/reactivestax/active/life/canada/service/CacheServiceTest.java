package io.reactivestax.active.life.canada.service;

import io.reactivestax.active.life.canada.constant.TestData;
import io.reactivestax.active.life.canada.dto.CourseEnrollmentWaitlistDto;
import io.reactivestax.active.life.canada.exception.InvalidRequestException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
class CacheServiceTest {

    @Autowired
    private CacheService cacheService;

    @Test
    void testAddToCache() {
        CourseEnrollmentWaitlistDto cartDto = CourseEnrollmentWaitlistDto.builder()
                .familyMemberLoginId(TestData.MEMBER_LOGIN_ID)
                .offeredCourseBarCode(TestData.BAR_CODE_STRING)
                .build();
        List<CourseEnrollmentWaitlistDto> courseEnrollmentWaitlistDtos = cacheService
                .addToCache(TestData.LOGGED_IN_MEMBER_ID_STRING, cartDto);

        assertEquals(1, courseEnrollmentWaitlistDtos.size());
        assertThrows(InvalidRequestException.class, () ->
                cacheService.addToCache(TestData.LOGGED_IN_MEMBER_ID_STRING, cartDto));
    }

    @Test
    void testClearCartAndGetCart(){
        cacheService.clearCart(TestData.LOGGED_IN_MEMBER_ID_STRING);
        List<CourseEnrollmentWaitlistDto> cart = cacheService.getCart(TestData.LOGGED_IN_MEMBER_ID_STRING);

        assertEquals(0, cart.size());
    }
}
