package io.reactivestax.active.life.canada.controller;

import io.reactivestax.active.life.canada.constant.Endpoints;
import io.reactivestax.active.life.canada.constant.ShortConstant;
import io.reactivestax.active.life.canada.constant.TestData;
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

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(DashboardController.class)
class DashboardControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CourseRegistrationManagementService courseRegistrationManagementService;

    @MockitoBean
    private ActiveLifeUtil activeLifeUtil;

    private static final String FAMILY_MEMBER_ID = TestData.LOGGED_IN_MEMBER_ID_STRING;
    private static final String SECURITY_HEADER_JSON = TestData.SECURITY_HEADER_JSON;

    @Test
    void testDashboard_Success() throws Exception {
        FamilyCourseRegistrationDetails courseDetails = new FamilyCourseRegistrationDetails();
        OfferedCourseWaitlistDto waitlistDto = new OfferedCourseWaitlistDto();

        when(activeLifeUtil.getSecurityHeader(anyString())).thenReturn(new SecurityHeader(FAMILY_MEMBER_ID));
        when(courseRegistrationManagementService.getRegisteredCourses(FAMILY_MEMBER_ID)).thenReturn(List.of(courseDetails));
        when(courseRegistrationManagementService.getWaitlistedCourses(FAMILY_MEMBER_ID)).thenReturn(List.of(waitlistDto));

        mockMvc.perform(get(Endpoints.BASE_ENDPOINT + Endpoints.DASHBOARD)
                        .header(ShortConstant.SECURITY_HEADER, SECURITY_HEADER_JSON)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.registeredCourses").isArray())
                .andExpect(jsonPath("$.waitlistedCourses").isArray());
    }
}
