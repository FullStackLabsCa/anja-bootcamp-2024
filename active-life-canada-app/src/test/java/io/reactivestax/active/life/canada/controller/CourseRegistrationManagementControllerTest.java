package io.reactivestax.active.life.canada.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.reactivestax.active.life.canada.constant.Endpoints;
import io.reactivestax.active.life.canada.constant.Message;
import io.reactivestax.active.life.canada.constant.ShortConstant;
import io.reactivestax.active.life.canada.constant.TestData;
import io.reactivestax.active.life.canada.dto.CartResponse;
import io.reactivestax.active.life.canada.dto.CourseEnrollmentWaitlistDto;
import io.reactivestax.active.life.canada.dto.FamilyCourseRegistrationDetails;
import io.reactivestax.active.life.canada.dto.OfferedCourseWaitlistDto;
import io.reactivestax.active.life.canada.model.SecurityHeader;
import io.reactivestax.active.life.canada.service.CourseRegistrationManagementService;
import io.reactivestax.active.life.canada.util.ActiveLifeUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CourseRegistrationManagementController.class)
class CourseRegistrationManagementControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private CourseRegistrationManagementService courseRegistrationManagementService;

    @MockitoBean
    private ActiveLifeUtil activeLifeUtil;

    private static final String SECURITY_HEADER_JSON = TestData.SECURITY_HEADER_JSON;
    private static final String FAMILY_MEMBER_ID = TestData.LOGGED_IN_MEMBER_ID_STRING;
    private static final String ENROLLMENT_ID = TestData.STRING_ID;

    @Test
    void testAddToCart_Success() throws Exception {
        when(activeLifeUtil.getSecurityHeader(anyString())).thenReturn(new SecurityHeader(FAMILY_MEMBER_ID));
        doNothing().when(courseRegistrationManagementService).addToCart(any(CourseEnrollmentWaitlistDto.class), anyString());

        mockMvc.perform(post(Endpoints.BASE_ENDPOINT + Endpoints.OFFERED_COURSE_CART)
                        .header(ShortConstant.SECURITY_HEADER, SECURITY_HEADER_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new CourseEnrollmentWaitlistDto())))
                .andExpect(status().isOk())
                .andExpect(jsonPath(TestData.JSON_EXPRESSION_MESSAGE).value(Message.ADDED_TO_CART));
    }

    @Test
    void testGetCart_Success() throws Exception {
        when(activeLifeUtil.getSecurityHeader(anyString())).thenReturn(new SecurityHeader(FAMILY_MEMBER_ID));
        doReturn(List.of(new CartResponse())).when(courseRegistrationManagementService).getCart(anyString());

        mockMvc.perform(get(Endpoints.BASE_ENDPOINT + Endpoints.OFFERED_COURSE_CART)
                        .header(ShortConstant.SECURITY_HEADER, SECURITY_HEADER_JSON)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath(TestData.JSON_EXPRESSION_FIRST_INDEX).isNotEmpty());
    }

    @Test
    void testPayForCart_Success() throws Exception {
        when(activeLifeUtil.getSecurityHeader(anyString())).thenReturn(new SecurityHeader(FAMILY_MEMBER_ID));
        doNothing().when(courseRegistrationManagementService).payForCart(anyString());

        mockMvc.perform(post(Endpoints.BASE_ENDPOINT + Endpoints.CART_PAYMENT)
                        .header(ShortConstant.SECURITY_HEADER, SECURITY_HEADER_JSON)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath(TestData.JSON_EXPRESSION_MESSAGE).value(Message.PAID_FOR_CART));
    }

    @Test
    void testRegisteredCourses_Success() throws Exception {
        FamilyCourseRegistrationDetails courseDetails = new FamilyCourseRegistrationDetails();
        when(activeLifeUtil.getSecurityHeader(anyString())).thenReturn(new SecurityHeader(FAMILY_MEMBER_ID));
        when(courseRegistrationManagementService.getRegisteredCourses(anyString())).thenReturn(List.of(courseDetails));

        mockMvc.perform(get(Endpoints.BASE_ENDPOINT + Endpoints.REGISTERED_COURSES)
                        .header(ShortConstant.SECURITY_HEADER, SECURITY_HEADER_JSON)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath(TestData.JSON_EXPRESSION_FIRST_INDEX).isNotEmpty());
    }

    @Test
    void testWaitlistedCourses_Success() throws Exception {
        OfferedCourseWaitlistDto waitlistDto = new OfferedCourseWaitlistDto();
        when(activeLifeUtil.getSecurityHeader(anyString())).thenReturn(new SecurityHeader(FAMILY_MEMBER_ID));
        when(courseRegistrationManagementService.getWaitlistedCourses(anyString())).thenReturn(List.of(waitlistDto));

        mockMvc.perform(get(Endpoints.BASE_ENDPOINT + Endpoints.WAITLISTED_COURSES)
                        .header(ShortConstant.SECURITY_HEADER, SECURITY_HEADER_JSON)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath(TestData.JSON_EXPRESSION_FIRST_INDEX).isNotEmpty());
    }

    @Test
    void testAddToWaitlist() throws Exception {
        when(activeLifeUtil.getSecurityHeader(anyString())).thenReturn(new SecurityHeader(FAMILY_MEMBER_ID));
        doNothing().when(courseRegistrationManagementService).addToWaitlist(anyString(),
                any(CourseEnrollmentWaitlistDto.class));

        mockMvc.perform(post(Endpoints.BASE_ENDPOINT + Endpoints.WAITLISTED_COURSES)
                        .header(ShortConstant.SECURITY_HEADER, SECURITY_HEADER_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new CourseEnrollmentWaitlistDto())))
                .andExpect(status().isOk())
                .andExpect(jsonPath(TestData.JSON_EXPRESSION_MESSAGE).value(Message.ADDED_TO_WAITLIST));
    }

    @Test
    void testWithdrawFromCourse_Success() throws Exception {
        when(activeLifeUtil.getSecurityHeader(anyString())).thenReturn(new SecurityHeader(FAMILY_MEMBER_ID));
        doNothing().when(courseRegistrationManagementService).withdrawFromCourse(anyString(), anyString());

        mockMvc.perform(delete(Endpoints.BASE_ENDPOINT + Endpoints.WITHDRAW_FROM_COURSE, ENROLLMENT_ID)
                        .header(ShortConstant.SECURITY_HEADER, SECURITY_HEADER_JSON)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath(TestData.JSON_EXPRESSION_MESSAGE).value(Message.WITHDRAWN_SUCCESSFUL));
    }
}
