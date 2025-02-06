package io.reactivestax.active.life.canada.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.reactivestax.active.life.canada.constant.Endpoints;
import io.reactivestax.active.life.canada.constant.Message;
import io.reactivestax.active.life.canada.constant.TestData;
import io.reactivestax.active.life.canada.dto.CreateMemberRequest;
import io.reactivestax.active.life.canada.dto.LoginMemberRequest;
import io.reactivestax.active.life.canada.dto.TokenResponseDto;
import io.reactivestax.active.life.canada.dto.TwoFactorLoginRequest;
import io.reactivestax.active.life.canada.service.AuthenticationManagementService;
import io.reactivestax.active.life.canada.service.FamilyManagementService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthenticationController.class)
class AuthenticationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private FamilyManagementService familyManagementService;

    @MockitoBean
    private AuthenticationManagementService authenticationManagementService;

    @Test
    void testSignUp_Success() throws Exception {
        CreateMemberRequest request = CreateMemberRequest.builder()
                .name(TestData.MEMBER_NAME)
                .username(TestData.USERNAME)
                .streetNo(TestData.STREET_NO)
                .streetName(TestData.STREET_NAME)
                .city(TestData.CITY1)
                .province(TestData.PROVINCE)
                .country(TestData.COUNTRY)
                .build();
        doNothing().when(familyManagementService).createFamilyMember(any(CreateMemberRequest.class), anyString(), anyBoolean());

        mockMvc.perform(post(Endpoints.BASE_ENDPOINT + Endpoints.SIGNUP)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath(TestData.JSON_EXPRESSION_MESSAGE).value(Message.SIGNUP_SUCCESSFUL));
    }

    @Test
    void testLogin_Success() throws Exception {
        LoginMemberRequest request = new LoginMemberRequest(TestData.USERNAME, TestData.PASSWORD);
        TokenResponseDto response = new TokenResponseDto();
        when(authenticationManagementService.loginMember(any(LoginMemberRequest.class))).thenReturn(response);

        mockMvc.perform(post(Endpoints.BASE_ENDPOINT + Endpoints.LOGIN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    void testTwoFactorLogin_Success() throws Exception {
        TwoFactorLoginRequest request = new TwoFactorLoginRequest();
        request.setToken(TestData.STRING_ID);
        request.setOtp(TestData.OTP);
        TokenResponseDto response = new TokenResponseDto();
        when(authenticationManagementService.twoFactorLogin(any(TwoFactorLoginRequest.class))).thenReturn(response);

        mockMvc.perform(post(Endpoints.BASE_ENDPOINT + Endpoints.LOGIN_2FA)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    void testActivateAccount_Success() throws Exception {
        String activationId = TestData.STRING_ID;
        doNothing().when(authenticationManagementService).activateMemberAccount(activationId);

        mockMvc.perform(get(Endpoints.BASE_ENDPOINT + Endpoints.ACTIVATION, activationId))
                .andExpect(status().isOk())
                .andExpect(jsonPath(TestData.JSON_EXPRESSION_MESSAGE).value(Message.ACTIVATED_SUCCESSFULLY));
    }
}
