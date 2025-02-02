package io.reactivestax.active.life.canada.service;

import io.reactivestax.active.life.canada.constant.Message;
import io.reactivestax.active.life.canada.constant.TestData;
import io.reactivestax.active.life.canada.dto.LoginMemberRequest;
import io.reactivestax.active.life.canada.dto.LoginResponse;
import io.reactivestax.active.life.canada.dto.TwoFactorLoginRequest;
import io.reactivestax.active.life.canada.entity.AccountActivationRequest;
import io.reactivestax.active.life.canada.entity.FamilyGroup;
import io.reactivestax.active.life.canada.entity.FamilyMember;
import io.reactivestax.active.life.canada.entity.LoginRequest;
import io.reactivestax.active.life.canada.exception.InvalidRequestException;
import io.reactivestax.active.life.canada.exception.SomethingWentWrongException;
import io.reactivestax.active.life.canada.repository.AccountActivationRequestRepository;
import io.reactivestax.active.life.canada.repository.FamilyMemberRepository;
import io.reactivestax.active.life.canada.repository.LoginRequestRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
class AuthenticationManagementServiceTest {

    @Autowired
    private AuthenticationManagementService authenticationManagementService;

    @MockitoBean
    private FamilyMemberRepository familyMemberRepository;

    @MockitoBean
    private LoginRequestRepository loginRequestRepository;

    @MockitoBean
    private AccountActivationRequestRepository accountActivationRequestRepository;

    @MockitoBean
    private EmsService emsService;

    @MockitoBean
    private AsyncJobsService asyncJobsService;

    private FamilyMember familyMember;

    @BeforeEach
    void setUp() {
        FamilyGroup familyGroup = mock(FamilyGroup.class);
        when(familyGroup.getFamilyPin()).thenReturn(TestData.PASSWORD);

        familyMember = FamilyMember.builder()
                .familyMemberId(TestData.FAMILY_MEMBER_ID_UUID)
                .isActive(true)
                .familyGroup(familyGroup)
                .build();
    }

    @Test
    void testLoginMember_SuccessfulLogin() {
        LoginMemberRequest request = new LoginMemberRequest(TestData.USERNAME, TestData.PASSWORD);
        when(familyMemberRepository.findByMemberLoginId(TestData.USERNAME)).thenReturn(Optional.of(familyMember));

        LoginResponse response = authenticationManagementService.loginMember(request);

        assertNotNull(response.getToken());
        assertEquals(Message.SUCCESSFUL_LOGIN, response.getMessage());
        verify(loginRequestRepository, times(1)).save(any(LoginRequest.class));
        verify(emsService, times(1)).sendToEmsOtp(familyMember);
    }

    @Test
    void testLoginMember_IncorrectUsername_ThrowsException() {
        LoginMemberRequest request = new LoginMemberRequest(TestData.WRONG_USERNAME, TestData.PASSWORD);

        when(familyMemberRepository.findByMemberLoginId(TestData.WRONG_USERNAME)).thenReturn(Optional.empty());

        assertThrows(InvalidRequestException.class, () -> authenticationManagementService.loginMember(request));
    }

    @Test
    void testLoginMember_IncorrectPassword_ThrowsException() {
        LoginMemberRequest request = new LoginMemberRequest(TestData.USERNAME, TestData.WRONG_PASSWORD);

        when(familyMemberRepository.findByMemberLoginId(TestData.USERNAME)).thenReturn(Optional.of(familyMember));

        assertThrows(InvalidRequestException.class, () -> authenticationManagementService.loginMember(request));
    }

    @Test
    void testLoginMember_InactiveMember_TriggersAccountActivation() {
        familyMember.setActive(false);
        LoginMemberRequest request = new LoginMemberRequest(TestData.USERNAME, TestData.PASSWORD);

        when(familyMemberRepository.findByMemberLoginId(TestData.USERNAME)).thenReturn(Optional.of(familyMember));

        LoginResponse response = authenticationManagementService.loginMember(request);

        assertEquals(Message.LOGIN_INACTIVE_MEMBER, response.getMessage());
        verify(asyncJobsService, times(1)).createAccountActivationRequestEntryAndSendToEms(familyMember);
    }

    @Test
    void testTwoFactorLogin_Success() {
        TwoFactorLoginRequest request = new TwoFactorLoginRequest(TestData.UUID_TOKEN_STRING, TestData.OTP);
        LoginRequest loginRequest = LoginRequest.builder()
                .familyMemberId(TestData.FAMILY_MEMBER_ID_UUID)
                .loginToken(TestData.UUID_TOKEN_STRING)
                .createdTs(LocalDateTime.now()).build();

        when(loginRequestRepository.findByLoginToken(TestData.UUID_TOKEN_STRING)).thenReturn(Optional.of(loginRequest));
        when(emsService.sendToEmsForVerification(TestData.FAMILY_MEMBER_ID_STRING, TestData.OTP)).thenReturn(true);

        LoginResponse response = authenticationManagementService.twoFactorLogin(request);

        assertEquals(TestData.FAMILY_MEMBER_ID_STRING, response.getToken());
        assertEquals(Message.SUCCESSFUL_LOGIN_VERIFICATION, response.getMessage());
    }

    @Test
    void testTwoFactorLogin_InvalidToken_ThrowsException() {
        when(loginRequestRepository.findByLoginToken(TestData.INVALID_TOKEN)).thenReturn(Optional.empty());

        TwoFactorLoginRequest request = new TwoFactorLoginRequest(TestData.INVALID_TOKEN, TestData.OTP);

        assertThrows(InvalidRequestException.class, () -> authenticationManagementService.twoFactorLogin(request));
    }

    @Test
    void testTwoFactorLogin_IncorrectOTP_ThrowsException() {
        TwoFactorLoginRequest request = new TwoFactorLoginRequest(TestData.UUID_TOKEN_STRING, TestData.WRONG_OTP);
        LoginRequest loginRequest = LoginRequest.builder()
                .familyMemberId(TestData.FAMILY_MEMBER_ID_UUID)
                .loginToken(TestData.UUID_TOKEN_STRING)
                .createdTs(LocalDateTime.now()).build();

        when(loginRequestRepository.findByLoginToken(TestData.UUID_TOKEN_STRING)).thenReturn(Optional.of(loginRequest));
        when(emsService.sendToEmsForVerification(TestData.FAMILY_MEMBER_ID_STRING, TestData.WRONG_OTP)).thenReturn(false);

        assertThrows(SomethingWentWrongException.class, () -> authenticationManagementService.twoFactorLogin(request));
    }

    @Test
    void testActivateMemberAccount_Success() {
        AccountActivationRequest activationRequest = AccountActivationRequest.builder()
                .accountActivationRequestId(TestData.UUID_ID)
                .familyMemberId(TestData.FAMILY_MEMBER_ID_UUID)
                .token(TestData.UUID_TOKEN)
                .createdTs(LocalDateTime.now())
                .build();

        when(accountActivationRequestRepository.findByToken(TestData.UUID_TOKEN)).thenReturn(Optional.of(activationRequest));

        authenticationManagementService.activateMemberAccount(TestData.UUID_TOKEN_STRING);

        verify(familyMemberRepository, times(1)).updateIsActiveByFamilyMemberId(TestData.FAMILY_MEMBER_ID_UUID, true);
    }

    @Test
    void testActivateMemberAccount_InvalidToken_ThrowsException() {
        when(accountActivationRequestRepository.findByToken(any())).thenReturn(Optional.empty());

        assertThrows(InvalidRequestException.class,
                () -> authenticationManagementService.activateMemberAccount(TestData.STRING_ID));
    }

    @Test
    void testActivateMemberAccount_ExpiredToken_ThrowsException() {
        AccountActivationRequest activationRequest = AccountActivationRequest.builder()
                .accountActivationRequestId(TestData.UUID_ID)
                .familyMemberId(TestData.FAMILY_MEMBER_ID_UUID)
                .token(TestData.UUID_TOKEN)
                .createdTs(LocalDateTime.now().minusMinutes(5))
                .build();

        when(accountActivationRequestRepository.findByToken(TestData.UUID_TOKEN)).thenReturn(Optional.of(activationRequest));

        assertThrows(InvalidRequestException.class,
                () -> authenticationManagementService.activateMemberAccount(TestData.UUID_TOKEN_STRING));
    }
}
