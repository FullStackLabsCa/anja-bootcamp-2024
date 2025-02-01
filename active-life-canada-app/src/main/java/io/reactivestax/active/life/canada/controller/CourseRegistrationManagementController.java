package io.reactivestax.active.life.canada.controller;


import io.reactivestax.active.life.canada.constant.Endpoints;
import io.reactivestax.active.life.canada.constant.Message;
import io.reactivestax.active.life.canada.constant.ShortConstant;
import io.reactivestax.active.life.canada.dto.FamilyCourseRegistrationDetails;
import io.reactivestax.active.life.canada.dto.OfferedCourseWaitlistDto;
import io.reactivestax.active.life.canada.dto.SuccessfulResponse;
import io.reactivestax.active.life.canada.model.SecurityHeader;
import io.reactivestax.active.life.canada.service.CourseRegistrationManagementService;
import io.reactivestax.active.life.canada.util.ActiveLifeUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(Endpoints.BASE_ENDPOINT)
public class CourseRegistrationManagementController {

    private final CourseRegistrationManagementService courseRegistrationManagementService;
    private final ActiveLifeUtil activeLifeUtil;

    public CourseRegistrationManagementController(CourseRegistrationManagementService courseRegistrationManagementService,
                                                  ActiveLifeUtil activeLifeUtil) {
        this.courseRegistrationManagementService = courseRegistrationManagementService;
        this.activeLifeUtil = activeLifeUtil;
    }

    @PostMapping(Endpoints.ENROLL_COURSE)
    public ResponseEntity<SuccessfulResponse> enrollIntoCourse(@PathVariable String barCode,
                                                               @PathVariable String memberLoginId,
                                                               @RequestHeader(name = ShortConstant.SECURITY_HEADER) String securityHeaderJson) {
        SecurityHeader securityHeader = activeLifeUtil.getSecurityHeader(securityHeaderJson);
        String message = courseRegistrationManagementService.enrollIntoOfferedCourse(barCode, memberLoginId,
                securityHeader.getFamilyMemberId());

        return ResponseEntity.ok(SuccessfulResponse.builder().message(message).build());
    }

    @GetMapping(Endpoints.REGISTERED_COURSES)
    public ResponseEntity<List<FamilyCourseRegistrationDetails>> registeredCourses(@RequestHeader(name = ShortConstant.SECURITY_HEADER) String securityHeaderJson) {
        SecurityHeader securityHeader = activeLifeUtil.getSecurityHeader(securityHeaderJson);
        List<FamilyCourseRegistrationDetails> familyCourseRegistrationDetailsList = this.courseRegistrationManagementService.getRegisteredCourses(securityHeader.getFamilyMemberId());

        return ResponseEntity.ok(familyCourseRegistrationDetailsList);
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
