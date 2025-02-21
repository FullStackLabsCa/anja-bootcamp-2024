package io.reactivestax.active.life.canada.controller;

import io.reactivestax.active.life.canada.constant.Endpoints;
import io.reactivestax.active.life.canada.dto.DashboardDto;
import io.reactivestax.active.life.canada.dto.FamilyCourseRegistrationDetails;
import io.reactivestax.active.life.canada.dto.OfferedCourseWaitlistDto;
import io.reactivestax.active.life.canada.service.AuthenticationManagementService;
import io.reactivestax.active.life.canada.service.CourseRegistrationManagementService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(Endpoints.BASE_ENDPOINT + Endpoints.DASHBOARD)
@RequiredArgsConstructor
public class DashboardController {

    private final CourseRegistrationManagementService courseRegistrationManagementService;
    private final AuthenticationManagementService authenticationManagementService;

    @GetMapping
    public ResponseEntity<DashboardDto> dashboard() {
        String loggedInMemberId = authenticationManagementService.getLoggedInMemberUsername();
        List<FamilyCourseRegistrationDetails> registeredCourses = courseRegistrationManagementService.getRegisteredCourses(loggedInMemberId);
        List<OfferedCourseWaitlistDto> waitlistedCourses = courseRegistrationManagementService.getWaitlistedCourses(loggedInMemberId);

        return ResponseEntity.ok(DashboardDto.builder()
                .registeredCourses(registeredCourses)
                .waitlistedCourses(waitlistedCourses)
                .build());
    }
}
