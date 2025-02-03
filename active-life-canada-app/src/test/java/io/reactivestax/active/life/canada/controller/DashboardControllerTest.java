package io.reactivestax.active.life.canada.controller;

import io.reactivestax.active.life.canada.constant.TestData;
import io.reactivestax.active.life.canada.dto.DashboardDto;
import io.reactivestax.active.life.canada.dto.FamilyCourseRegistrationDetails;
import io.reactivestax.active.life.canada.dto.OfferedCourseWaitlistDto;
import io.reactivestax.active.life.canada.model.SecurityHeader;
import io.reactivestax.active.life.canada.service.CourseRegistrationManagementService;
import io.reactivestax.active.life.canada.util.ActiveLifeUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@WebMvcTest(DashboardController.class)
class DashboardControllerTest {

    @Autowired
    private DashboardController dashboardController;

    @MockitoBean
    private CourseRegistrationManagementService courseRegistrationManagementService;

    @MockitoBean
    private ActiveLifeUtil activeLifeUtil;

    private final String securityHeaderJson = TestData.SECURITY_HEADER_JSON;
    private final String loggedInMemberId = TestData.LOGGED_IN_MEMBER_ID_STRING;

    @Test
    void testDashboard_Success() {
        SecurityHeader securityHeader = new SecurityHeader();
        securityHeader.setFamilyMemberId(loggedInMemberId);

        List<FamilyCourseRegistrationDetails> registeredCourses = List.of(new FamilyCourseRegistrationDetails());
        List<OfferedCourseWaitlistDto> waitlistedCourses = List.of(new OfferedCourseWaitlistDto());

        when(activeLifeUtil.getSecurityHeader(securityHeaderJson)).thenReturn(securityHeader);
        when(courseRegistrationManagementService.getRegisteredCourses(loggedInMemberId)).thenReturn(registeredCourses);
        when(courseRegistrationManagementService.getWaitlistedCourses(loggedInMemberId)).thenReturn(waitlistedCourses);

        ResponseEntity<DashboardDto> response = dashboardController.dashboard(securityHeaderJson);

        assertEquals(200, response.getStatusCode().value());
        assertEquals(registeredCourses, Objects.requireNonNull(response.getBody()).getRegisteredCourses());
        assertEquals(waitlistedCourses, response.getBody().getWaitlistedCourses());

        verify(activeLifeUtil, times(1)).getSecurityHeader(securityHeaderJson);
        verify(courseRegistrationManagementService, times(1)).getRegisteredCourses(loggedInMemberId);
        verify(courseRegistrationManagementService, times(1)).getWaitlistedCourses(loggedInMemberId);
    }
}
