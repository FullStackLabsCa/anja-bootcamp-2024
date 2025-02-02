package io.reactivestax.active.life.canada.controller;

import io.reactivestax.active.life.canada.constant.Endpoints;
import io.reactivestax.active.life.canada.constant.ShortConstant;
import io.reactivestax.active.life.canada.dto.DashboardDto;
import io.reactivestax.active.life.canada.dto.FamilyCourseRegistrationDetails;
import io.reactivestax.active.life.canada.dto.OfferedCourseWaitlistDto;
import io.reactivestax.active.life.canada.model.SecurityHeader;
import io.reactivestax.active.life.canada.service.CourseRegistrationManagementService;
import io.reactivestax.active.life.canada.util.ActiveLifeUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(Endpoints.BASE_ENDPOINT + Endpoints.DASHBOARD)
@RequiredArgsConstructor
public class DashboardController {

    private final CourseRegistrationManagementService courseRegistrationManagementService;
    private final ActiveLifeUtil activeLifeUtil;

    @GetMapping
    public ResponseEntity<DashboardDto> dashboard(@RequestHeader(name = ShortConstant.SECURITY_HEADER) String securityHeaderJson) {
        SecurityHeader securityHeader = activeLifeUtil.getSecurityHeader(securityHeaderJson);
        String loggedInMemberId = securityHeader.getFamilyMemberId();
        List<FamilyCourseRegistrationDetails> registeredCourses = courseRegistrationManagementService.getRegisteredCourses(loggedInMemberId);
        List<OfferedCourseWaitlistDto> waitlistedCourses = courseRegistrationManagementService.getWaitlistedCourses(loggedInMemberId);

        return ResponseEntity.ok(DashboardDto.builder()
                .registeredCourses(registeredCourses)
                .waitlistedCourses(waitlistedCourses)
                .build());
    }
}
