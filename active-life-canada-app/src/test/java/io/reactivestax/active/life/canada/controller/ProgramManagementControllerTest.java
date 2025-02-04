package io.reactivestax.active.life.canada.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.reactivestax.active.life.canada.constant.Endpoints;
import io.reactivestax.active.life.canada.constant.Message;
import io.reactivestax.active.life.canada.constant.TestData;
import io.reactivestax.active.life.canada.dto.*;
import io.reactivestax.active.life.canada.service.ProgramManagementService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ProgramManagementController.class)
class ProgramManagementControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private ProgramManagementService programManagementService;

    @Test
    void testOfferCourse_Success() throws Exception {
        OfferCourseRequest request = OfferCourseRequest.builder()
                .courseId(1L)
                .facilityId(1L)
                .noOfSpots(10)
                .noOfClassesOffered(10)
                .isAllDayCourse(true)
                .residentCourseFee(100)
                .nonResidentCourseFee(180)
                .build();
       doNothing().when(programManagementService).offerCourse(any(OfferCourseRequest.class));

        mockMvc.perform(post(Endpoints.BASE_ENDPOINT + Endpoints.OFFERED_COURSES)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath(TestData.JSON_EXPRESSION_MESSAGE).value(Message.OFFERED_COURSE_ADDED));
    }

    @Test
    void testUpdateOfferedCourse_Success() throws Exception {
        CourseUpdateRequest request = new CourseUpdateRequest();
        doNothing().when(programManagementService).updateOfferedCourse(any(CourseUpdateRequest.class));

        mockMvc.perform(put(Endpoints.BASE_ENDPOINT + Endpoints.OFFERED_COURSES)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath(TestData.JSON_EXPRESSION_MESSAGE).value(Message.OFFERED_COURSE_UPDATED));
    }

    @Test
    void testGetOfferedCourses_Success() throws Exception {
        OfferedCourseDetailsResponse mockCourseResponse = new OfferedCourseDetailsResponse();
        when(programManagementService.offeredCourses()).thenReturn(List.of(mockCourseResponse));

        mockMvc.perform(get(Endpoints.BASE_ENDPOINT + Endpoints.OFFERED_COURSES)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath(TestData.JSON_EXPRESSION_FIRST_INDEX).isNotEmpty());
    }

    @Test
    void testSearchOfferedCourses_Success() throws Exception {
        OfferedCourseSearchRequest searchRequest = new OfferedCourseSearchRequest();
        OfferedCourseDetailsResponse mockCourseResponse = new OfferedCourseDetailsResponse();
        when(programManagementService.searchOfferedCourses(any(OfferedCourseSearchRequest.class)))
                .thenReturn(List.of(mockCourseResponse));

        mockMvc.perform(post(Endpoints.BASE_ENDPOINT + Endpoints.SEARCH_OFFERED_COURSES)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(searchRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath(TestData.JSON_EXPRESSION_FIRST_INDEX).isNotEmpty());
    }
}
