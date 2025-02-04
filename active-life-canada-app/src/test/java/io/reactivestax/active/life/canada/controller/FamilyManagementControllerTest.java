package io.reactivestax.active.life.canada.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.reactivestax.active.life.canada.constant.Endpoints;
import io.reactivestax.active.life.canada.constant.Message;
import io.reactivestax.active.life.canada.constant.ShortConstant;
import io.reactivestax.active.life.canada.constant.TestData;
import io.reactivestax.active.life.canada.dto.*;
import io.reactivestax.active.life.canada.enums.Gender;
import io.reactivestax.active.life.canada.model.SecurityHeader;
import io.reactivestax.active.life.canada.service.FamilyManagementService;
import io.reactivestax.active.life.canada.util.ActiveLifeUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(FamilyManagementController.class)
class FamilyManagementControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private FamilyManagementService familyManagementService;

    @MockitoBean
    private ActiveLifeUtil activeLifeUtil;

    private static final String MEMBER_LOGIN_ID = TestData.MEMBER_LOGIN_ID;
    private static final String SECURITY_HEADER_JSON = TestData.SECURITY_HEADER_JSON;
    private static final String FAMILY_MEMBER_ID = TestData.LOGGED_IN_MEMBER_ID_STRING;

    @Test
    void testAddMember_Success() throws Exception {
        CreateMemberRequest request = CreateMemberRequest.builder()
                .name(TestData.MEMBER_NAME)
                .dob(LocalDate.now())
                .gender(Gender.MALE)
                .emailId(TestData.EMAIL)
                .streetNo("123")
                .streetName("Lester")
                .city(TestData.CITY1)
                .province(TestData.PROVINCE)
                .homePhone(TestData.HOME_PHONE)
                .username(TestData.USERNAME)
                .country(TestData.COUNTRY)

                .build();
        when(activeLifeUtil.getSecurityHeader(anyString())).thenReturn(new SecurityHeader(FAMILY_MEMBER_ID));
        doNothing().when(familyManagementService).createFamilyMember(any(CreateMemberRequest.class), anyString(), anyBoolean());

        mockMvc.perform(post(Endpoints.BASE_ENDPOINT + Endpoints.MEMBERS_BASE)
                        .header(ShortConstant.SECURITY_HEADER, SECURITY_HEADER_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath(TestData.JSON_EXPRESSION_MESSAGE).value(Message.MEMBER_ADD_SUCCESSFUL));
    }

    @Test
    void testUpdateMember_Success() throws Exception {
        UpdateMemberRequest request = new UpdateMemberRequest();
        when(activeLifeUtil.getSecurityHeader(anyString())).thenReturn(new SecurityHeader(FAMILY_MEMBER_ID));
        doNothing().when(familyManagementService).updateFamilyMember(anyString(), any(UpdateMemberRequest.class), anyString());

        mockMvc.perform(patch(Endpoints.BASE_ENDPOINT + Endpoints.MEMBERS_BASE + Endpoints.MEMBER_ID, MEMBER_LOGIN_ID)
                        .header(ShortConstant.SECURITY_HEADER, SECURITY_HEADER_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath(TestData.JSON_EXPRESSION_MESSAGE).value(Message.MEMBER_UPDATED));
    }

    @Test
    void testGetMember_Success() throws Exception {
        MemberDetails memberDetails = new MemberDetails();
        when(activeLifeUtil.getSecurityHeader(anyString())).thenReturn(new SecurityHeader(FAMILY_MEMBER_ID));
        when(familyManagementService.getFamilyMember(anyString(), anyString())).thenReturn(memberDetails);

        mockMvc.perform(get(Endpoints.BASE_ENDPOINT + Endpoints.MEMBERS_BASE + Endpoints.MEMBER_ID, MEMBER_LOGIN_ID)
                        .header(ShortConstant.SECURITY_HEADER, SECURITY_HEADER_JSON)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isNotEmpty());
    }

    @Test
    void testDeactivateMember_Success() throws Exception {
        when(activeLifeUtil.getSecurityHeader(anyString())).thenReturn(new SecurityHeader(FAMILY_MEMBER_ID));
        doNothing().when(familyManagementService).deactivateFamilyMember(anyString(), anyString());

        mockMvc.perform(delete(Endpoints.BASE_ENDPOINT + Endpoints.MEMBERS_BASE + Endpoints.MEMBER_ID, MEMBER_LOGIN_ID)
                        .header(ShortConstant.SECURITY_HEADER, SECURITY_HEADER_JSON)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath(TestData.JSON_EXPRESSION_MESSAGE).value(Message.MEMBER_DEACTIVATED));
    }
}
