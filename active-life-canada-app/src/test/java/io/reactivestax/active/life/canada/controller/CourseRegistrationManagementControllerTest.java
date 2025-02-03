package io.reactivestax.active.life.canada.controller;

import io.reactivestax.active.life.canada.constant.Message;
import io.reactivestax.active.life.canada.constant.TestData;
import io.reactivestax.active.life.canada.dto.FamilyCourseRegistrationDetails;
import io.reactivestax.active.life.canada.dto.OfferedCourseWaitlistDto;
import io.reactivestax.active.life.canada.dto.SuccessfulResponse;
import io.reactivestax.active.life.canada.model.SecurityHeader;
import io.reactivestax.active.life.canada.service.CourseRegistrationManagementService;
import io.reactivestax.active.life.canada.util.ActiveLifeUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@WebMvcTest(CourseRegistrationManagementController.class)
class CourseRegistrationManagementControllerTest {

    @Autowired
    private CourseRegistrationManagementController courseRegistrationManagementController;

    @MockitoBean
    private CourseRegistrationManagementService courseRegistrationManagementService;

    @MockitoBean
    private ActiveLifeUtil activeLifeUtil;

    private static final String SECURITY_HEADER_JSON = TestData.SECURITY_HEADER_JSON;
    private static final SecurityHeader SECURITY_HEADER = new SecurityHeader(TestData.LOGGED_IN_MEMBER_ID_STRING);

    @BeforeEach
    void setUp() {
        when(activeLifeUtil.getSecurityHeader(SECURITY_HEADER_JSON)).thenReturn(SECURITY_HEADER);
    }

    @Test
    void testEnrollIntoCourse() {
        String barCode = TestData.BAR_CODE_STRING;
        String memberLoginId = TestData.MEMBER_LOGIN_ID;
        String successMessage = Message.ENROLLMENT_SUCCESSFUL;

        when(courseRegistrationManagementService.enrollIntoOfferedCourse
                (barCode, memberLoginId, SECURITY_HEADER.getFamilyMemberId()))
                .thenReturn(successMessage);

        ResponseEntity<SuccessfulResponse> response = courseRegistrationManagementController.enrollIntoCourse(barCode, memberLoginId, SECURITY_HEADER_JSON);

        assertEquals(200, response.getStatusCode().value());
        assertEquals(successMessage, Objects.requireNonNull(response.getBody()).getMessage());
    }

    @Test
    void testRegisteredCourses() {
        List<FamilyCourseRegistrationDetails> mockList = Collections.singletonList(new FamilyCourseRegistrationDetails());
        when(courseRegistrationManagementService.getRegisteredCourses(SECURITY_HEADER.getFamilyMemberId()))
                .thenReturn(mockList);

        ResponseEntity<List<FamilyCourseRegistrationDetails>> response = courseRegistrationManagementController.registeredCourses(SECURITY_HEADER_JSON);

        assertEquals(200, response.getStatusCode().value());
        assertEquals(mockList, response.getBody());
    }

    @Test
    void testWaitlistedCourses() {
        List<OfferedCourseWaitlistDto> mockList = Collections.singletonList(new OfferedCourseWaitlistDto());
        when(courseRegistrationManagementService.getWaitlistedCourses(SECURITY_HEADER.getFamilyMemberId()))
                .thenReturn(mockList);

        ResponseEntity<List<OfferedCourseWaitlistDto>> response = courseRegistrationManagementController.waitlistedCourses(SECURITY_HEADER_JSON);

        assertEquals(200, response.getStatusCode().value());
        assertEquals(mockList, response.getBody());
    }

    @Test
    void testWithdrawFromCourse() {
        String enrollmentId = TestData.STRING_ID;

        ResponseEntity<SuccessfulResponse> response = courseRegistrationManagementController.withdrawFromCourse(enrollmentId, SECURITY_HEADER_JSON);

        verify(courseRegistrationManagementService).withdrawFromCourse(enrollmentId, SECURITY_HEADER.getFamilyMemberId());
        assertEquals(200, response.getStatusCode().value());
        assertEquals(Message.WITHDRAWN_SUCCESSFUL, Objects.requireNonNull(response.getBody()).getMessage());
    }
}
