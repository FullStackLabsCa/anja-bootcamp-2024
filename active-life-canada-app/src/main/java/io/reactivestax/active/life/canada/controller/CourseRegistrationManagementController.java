package io.reactivestax.active.life.canada.controller;


import io.reactivestax.active.life.canada.constant.Endpoints;
import io.reactivestax.active.life.canada.constant.Message;
import io.reactivestax.active.life.canada.constant.ShortConstant;
import io.reactivestax.active.life.canada.dto.*;
import io.reactivestax.active.life.canada.model.SecurityHeader;
import io.reactivestax.active.life.canada.service.CourseRegistrationManagementService;
import io.reactivestax.active.life.canada.util.ActiveLifeUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(Endpoints.BASE_ENDPOINT)
@RequiredArgsConstructor
public class CourseRegistrationManagementController {

    private final CourseRegistrationManagementService courseRegistrationManagementService;
    private final ActiveLifeUtil activeLifeUtil;

    @PostMapping(Endpoints.OFFERED_COURSE_CART)
    public ResponseEntity<SuccessfulResponse> addToCart
            (@RequestHeader(name = ShortConstant.SECURITY_HEADER) String securityHeaderJson,
             @RequestBody CourseEnrollmentWaitlistDto cartDto) {
        SecurityHeader securityHeader = activeLifeUtil.getSecurityHeader(securityHeaderJson);
        courseRegistrationManagementService.addToCart(cartDto, securityHeader.getFamilyMemberId());

        return ResponseEntity.ok(SuccessfulResponse.builder().message(Message.ADDED_TO_CART).build());
    }

    @GetMapping(Endpoints.OFFERED_COURSE_CART)
    public ResponseEntity<List<CartResponse>> getCart(@RequestHeader(name = ShortConstant.SECURITY_HEADER) String securityHeaderJson) {
        SecurityHeader securityHeader = activeLifeUtil.getSecurityHeader(securityHeaderJson);
        List<CartResponse> cart = courseRegistrationManagementService.getCart(securityHeader.getFamilyMemberId());

        return ResponseEntity.ok(cart);
    }

    @PostMapping(Endpoints.CART_PAYMENT)
    public ResponseEntity<SuccessfulResponse> payForCart(@RequestHeader(name = ShortConstant.SECURITY_HEADER) String securityHeaderJson) {
        SecurityHeader securityHeader = activeLifeUtil.getSecurityHeader(securityHeaderJson);
      courseRegistrationManagementService.payForCart(securityHeader.getFamilyMemberId());

        return ResponseEntity.ok(SuccessfulResponse.builder().message(Message.PAID_FOR_CART).build());
    }

    @GetMapping(Endpoints.REGISTERED_COURSES)
    public ResponseEntity<List<FamilyCourseRegistrationDetails>> registeredCourses(@RequestHeader(name = ShortConstant.SECURITY_HEADER) String securityHeaderJson) {
        SecurityHeader securityHeader = activeLifeUtil.getSecurityHeader(securityHeaderJson);
        List<FamilyCourseRegistrationDetails> familyCourseRegistrationDetailsList = this.courseRegistrationManagementService.getRegisteredCourses(securityHeader.getFamilyMemberId());

        return ResponseEntity.ok(familyCourseRegistrationDetailsList);
    }

    @PostMapping(Endpoints.WAITLISTED_COURSES)
    public ResponseEntity<SuccessfulResponse> addToWaitList
            (@RequestHeader(name = ShortConstant.SECURITY_HEADER) String securityHeaderJson,
             @RequestBody CourseEnrollmentWaitlistDto waitlistDto) {
        SecurityHeader securityHeader = activeLifeUtil.getSecurityHeader(securityHeaderJson);
        this.courseRegistrationManagementService.addToWaitlist(securityHeader.getFamilyMemberId(), waitlistDto);

        return ResponseEntity.ok(SuccessfulResponse.builder().message(Message.WITHDRAWN_SUCCESSFUL).build());
    }

    @GetMapping(Endpoints.WAITLISTED_COURSES)
    public ResponseEntity<List<OfferedCourseWaitlistDto>> waitlistedCourses(@RequestHeader(name = ShortConstant.SECURITY_HEADER) String securityHeaderJson) {
        SecurityHeader securityHeader = activeLifeUtil.getSecurityHeader(securityHeaderJson);
        List<OfferedCourseWaitlistDto> waitlistedCourses = this.courseRegistrationManagementService.getWaitlistedCourses(securityHeader.getFamilyMemberId());

        return ResponseEntity.ok(waitlistedCourses);
    }

    @DeleteMapping(Endpoints.WITHDRAW_FROM_COURSE)
    public ResponseEntity<SuccessfulResponse> withdrawFromCourse(@PathVariable String enrollmentId,
                                                                 @RequestHeader(name = ShortConstant.SECURITY_HEADER) String securityHeaderJson) {
        SecurityHeader securityHeader = activeLifeUtil.getSecurityHeader(securityHeaderJson);
        this.courseRegistrationManagementService.withdrawFromCourse(enrollmentId, securityHeader.getFamilyMemberId());

        return ResponseEntity.ok(SuccessfulResponse.builder().message(Message.WITHDRAWN_SUCCESSFUL).build());
    }
}
