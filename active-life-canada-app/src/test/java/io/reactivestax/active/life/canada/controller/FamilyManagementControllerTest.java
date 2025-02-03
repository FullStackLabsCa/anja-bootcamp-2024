package io.reactivestax.active.life.canada.controller;

import io.reactivestax.active.life.canada.constant.Message;
import io.reactivestax.active.life.canada.constant.TestData;
import io.reactivestax.active.life.canada.dto.CreateMemberRequest;
import io.reactivestax.active.life.canada.dto.MemberDetails;
import io.reactivestax.active.life.canada.dto.SuccessfulResponse;
import io.reactivestax.active.life.canada.dto.UpdateMemberRequest;
import io.reactivestax.active.life.canada.model.SecurityHeader;
import io.reactivestax.active.life.canada.service.FamilyManagementService;
import io.reactivestax.active.life.canada.util.ActiveLifeUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@WebMvcTest(FamilyManagementController.class)
class FamilyManagementControllerTest {

    @Autowired
    private FamilyManagementController familyManagementController;

    @MockitoBean
    private FamilyManagementService familyManagementService;

    @MockitoBean
    private ActiveLifeUtil activeLifeUtil;

    private static final String SECURITY_HEADER_JSON = TestData.SECURITY_HEADER_JSON;
    private static final SecurityHeader SECURITY_HEADER = new SecurityHeader(TestData.LOGGED_IN_MEMBER_ID_STRING);

    @BeforeEach
    void setUp() {
        when(activeLifeUtil.getSecurityHeader(SECURITY_HEADER_JSON)).thenReturn(SECURITY_HEADER);
    }

    @Test
    void testAddMember() {
        CreateMemberRequest createMemberRequest = new CreateMemberRequest();
        String successMessage = Message.MEMBER_ADD_SUCCESSFUL;

        doNothing().when(familyManagementService).createFamilyMember
                (any(CreateMemberRequest.class), anyString(), anyBoolean());

        ResponseEntity<SuccessfulResponse> response = familyManagementController
                .addMember(SECURITY_HEADER_JSON, createMemberRequest);

        assertEquals(200, response.getStatusCode().value());
        assertEquals(successMessage, Objects.requireNonNull(response.getBody()).getMessage());
    }

    @Test
    void testUpdateMember() {
        UpdateMemberRequest updateMemberRequest = new UpdateMemberRequest();

        String successMessage = Message.MEMBER_UPDATED;
        doNothing().when(familyManagementService).updateFamilyMember(anyString(), any(UpdateMemberRequest.class), anyString());

        ResponseEntity<SuccessfulResponse> response = familyManagementController
                .updateMember(TestData.MEMBER_LOGIN_ID, SECURITY_HEADER_JSON, updateMemberRequest);

        assertEquals(200, response.getStatusCode().value());
        assertEquals(successMessage, Objects.requireNonNull(response.getBody()).getMessage());
    }

    @Test
    void testGetMember() {
        MemberDetails mockMemberDetails = new MemberDetails();

        when(familyManagementService.getFamilyMember(anyString(), anyString())).thenReturn(mockMemberDetails);

        ResponseEntity<MemberDetails> response = familyManagementController.getMember(TestData.MEMBER_LOGIN_ID, SECURITY_HEADER_JSON);

        assertEquals(200, response.getStatusCode().value());
        assertEquals(mockMemberDetails, response.getBody());
    }

    @Test
    void testDeactivateMember() {
        String successMessage = Message.MEMBER_DEACTIVATED;

        doNothing().when(familyManagementService).deactivateFamilyMember(anyString(), anyString());

        ResponseEntity<SuccessfulResponse> response = familyManagementController
                .deactivateMember(TestData.MEMBER_LOGIN_ID, SECURITY_HEADER_JSON);

        verify(familyManagementService).deactivateFamilyMember(TestData.MEMBER_LOGIN_ID, SECURITY_HEADER.getFamilyMemberId());
        assertEquals(200, response.getStatusCode().value());
        assertEquals(successMessage, Objects.requireNonNull(response.getBody()).getMessage());
    }
}
