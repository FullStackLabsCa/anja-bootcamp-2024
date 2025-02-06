//package io.reactivestax.active.life.canada.controller;
//
//import io.reactivestax.active.life.canada.constant.Endpoints;
//import io.reactivestax.active.life.canada.constant.Message;
//import io.reactivestax.active.life.canada.constant.ShortConstant;
//import io.reactivestax.active.life.canada.constant.TestData;
//import io.reactivestax.active.life.canada.dto.FamilyCourseRegistrationDetails;
//import io.reactivestax.active.life.canada.dto.OfferedCourseWaitlistDto;
//import io.reactivestax.active.life.canada.model.SecurityHeader;
//import io.reactivestax.active.life.canada.service.CourseRegistrationManagementService;
//import io.reactivestax.active.life.canada.util.ActiveLifeUtil;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
//import org.springframework.http.MediaType;
//import org.springframework.test.context.bean.override.mockito.MockitoBean;
//import org.springframework.test.web.servlet.MockMvc;
//
//import java.util.List;
//
//import static org.mockito.ArgumentMatchers.anyString;
//import static org.mockito.Mockito.doNothing;
//import static org.mockito.Mockito.when;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
//
//@WebMvcTest(CourseRegistrationManagementController.class)
//class CourseRegistrationManagementControllerTest {
//
//    @Autowired
//    private MockMvc mockMvc;
//
//    @MockitoBean
//    private CourseRegistrationManagementService courseRegistrationManagementService;
//
//    @MockitoBean
//    private ActiveLifeUtil activeLifeUtil;
//
//    private static final String SECURITY_HEADER_JSON = TestData.SECURITY_HEADER_JSON;
//    private static final String FAMILY_MEMBER_ID = TestData.LOGGED_IN_MEMBER_ID_STRING;
//    private static final String ENROLLMENT_ID = TestData.STRING_ID;
//    private static final String BARCODE = TestData.BAR_CODE_STRING;
//    private static final String MEMBER_LOGIN_ID = TestData.MEMBER_LOGIN_ID;
//
//    @Test
//    void testEnrollIntoCourse_Success() throws Exception {
//        when(activeLifeUtil.getSecurityHeader(anyString())).thenReturn(new SecurityHeader(FAMILY_MEMBER_ID));
//        when(courseRegistrationManagementService.enrollIntoOfferedCourse(anyString(), anyString(), anyString()))
//                .thenReturn(Message.ENROLLMENT_SUCCESSFUL);
//
//        mockMvc.perform(post(Endpoints.BASE_ENDPOINT + Endpoints.ENROLL_COURSE, BARCODE, MEMBER_LOGIN_ID)
//                        .header(ShortConstant.SECURITY_HEADER, SECURITY_HEADER_JSON)
//                        .contentType(MediaType.APPLICATION_JSON))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath(TestData.JSON_EXPRESSION_MESSAGE).value(Message.ENROLLMENT_SUCCESSFUL));
//    }
//
//    @Test
//    void testRegisteredCourses_Success() throws Exception {
//        FamilyCourseRegistrationDetails courseDetails = new FamilyCourseRegistrationDetails();
//        when(activeLifeUtil.getSecurityHeader(anyString())).thenReturn(new SecurityHeader(FAMILY_MEMBER_ID));
//        when(courseRegistrationManagementService.getRegisteredCourses(anyString())).thenReturn(List.of(courseDetails));
//
//        mockMvc.perform(get(Endpoints.BASE_ENDPOINT + Endpoints.REGISTERED_COURSES)
//                        .header(ShortConstant.SECURITY_HEADER, SECURITY_HEADER_JSON)
//                        .contentType(MediaType.APPLICATION_JSON))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath(TestData.JSON_EXPRESSION_FIRST_INDEX).isNotEmpty());
//    }
//
//    @Test
//    void testWaitlistedCourses_Success() throws Exception {
//        OfferedCourseWaitlistDto waitlistDto = new OfferedCourseWaitlistDto();
//        when(activeLifeUtil.getSecurityHeader(anyString())).thenReturn(new SecurityHeader(FAMILY_MEMBER_ID));
//        when(courseRegistrationManagementService.getWaitlistedCourses(anyString())).thenReturn(List.of(waitlistDto));
//
//        mockMvc.perform(get(Endpoints.BASE_ENDPOINT + Endpoints.WAITLISTED_COURSES)
//                        .header(ShortConstant.SECURITY_HEADER, SECURITY_HEADER_JSON)
//                        .contentType(MediaType.APPLICATION_JSON))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath(TestData.JSON_EXPRESSION_FIRST_INDEX).isNotEmpty());
//    }
//
//    @Test
//    void testWithdrawFromCourse_Success() throws Exception {
//        when(activeLifeUtil.getSecurityHeader(anyString())).thenReturn(new SecurityHeader(FAMILY_MEMBER_ID));
//        doNothing().when(courseRegistrationManagementService).withdrawFromCourse(anyString(), anyString());
//
//        mockMvc.perform(delete(Endpoints.BASE_ENDPOINT + Endpoints.WITHDRAW_FROM_COURSE, ENROLLMENT_ID)
//                        .header(ShortConstant.SECURITY_HEADER, SECURITY_HEADER_JSON)
//                        .contentType(MediaType.APPLICATION_JSON))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath(TestData.JSON_EXPRESSION_MESSAGE).value(Message.WITHDRAWN_SUCCESSFUL));
//    }
//}
