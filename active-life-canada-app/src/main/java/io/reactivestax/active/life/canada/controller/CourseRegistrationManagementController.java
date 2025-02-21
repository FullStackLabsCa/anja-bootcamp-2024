package io.reactivestax.active.life.canada.controller;


import io.reactivestax.active.life.canada.constant.Endpoints;
import io.reactivestax.active.life.canada.constant.Message;
import io.reactivestax.active.life.canada.dto.*;
import io.reactivestax.active.life.canada.service.AuthenticationManagementService;
import io.reactivestax.active.life.canada.service.CourseRegistrationManagementService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(Endpoints.BASE_ENDPOINT)
@RequiredArgsConstructor
public class CourseRegistrationManagementController {

    private final CourseRegistrationManagementService courseRegistrationManagementService;
    private final AuthenticationManagementService authenticationManagementService;

    @PostMapping(Endpoints.OFFERED_COURSE_CART)
    public ResponseEntity<SuccessfulResponse> addToCart(@RequestBody CourseEnrollmentWaitlistDto cartDto) {
        courseRegistrationManagementService.addToCart(cartDto, authenticationManagementService.getLoggedInMemberUsername());

        return ResponseEntity.ok(SuccessfulResponse.builder().message(Message.ADDED_TO_CART).build());
    }

    @GetMapping(Endpoints.OFFERED_COURSE_CART)
    public ResponseEntity<List<CartResponse>> getCart() {
        List<CartResponse> cart = courseRegistrationManagementService
                .getCart(authenticationManagementService.getLoggedInMemberUsername());

        return ResponseEntity.ok(cart);
    }

    @PostMapping(Endpoints.CART_PAYMENT)
    public ResponseEntity<SuccessfulResponse> payForCart(@RequestBody PaymentDto paymentDto) {
        courseRegistrationManagementService.payForCart
                (authenticationManagementService.getLoggedInMemberUsername(), paymentDto);

        return ResponseEntity.ok(SuccessfulResponse.builder().message(Message.PAID_FOR_CART).build());
    }

    @GetMapping(Endpoints.REGISTERED_COURSES)
    public ResponseEntity<List<FamilyCourseRegistrationDetails>> registeredCourses() {
        List<FamilyCourseRegistrationDetails> familyCourseRegistrationDetailsList = this.courseRegistrationManagementService
                .getRegisteredCourses(authenticationManagementService.getLoggedInMemberUsername());

        return ResponseEntity.ok(familyCourseRegistrationDetailsList);
    }

    @PostMapping(Endpoints.WAITLISTED_COURSES)
    public ResponseEntity<SuccessfulResponse> addToWaitList(@RequestBody CourseEnrollmentWaitlistDto waitlistDto) {
        this.courseRegistrationManagementService.addToWaitlist
                (authenticationManagementService.getLoggedInMemberUsername(), waitlistDto);

        return ResponseEntity.ok(SuccessfulResponse.builder().message(Message.ADDED_TO_WAITLIST).build());
    }

    @GetMapping(Endpoints.WAITLISTED_COURSES)
    public ResponseEntity<List<OfferedCourseWaitlistDto>> waitlistedCourses() {
        List<OfferedCourseWaitlistDto> waitlistedCourses = this.courseRegistrationManagementService
                .getWaitlistedCourses(authenticationManagementService.getLoggedInMemberUsername());

        return ResponseEntity.ok(waitlistedCourses);
    }

    @DeleteMapping(Endpoints.WITHDRAW_FROM_COURSE)
    public ResponseEntity<SuccessfulResponse> withdrawFromCourse(@PathVariable String enrollmentId) {
        this.courseRegistrationManagementService.withdrawFromCourse
                (enrollmentId, authenticationManagementService.getLoggedInMemberUsername());

        return ResponseEntity.ok(SuccessfulResponse.builder().message(Message.WITHDRAWN_SUCCESSFUL).build());
    }
}
