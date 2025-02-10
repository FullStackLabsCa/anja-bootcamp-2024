package io.reactivestax.active.life.canada.service;

import io.reactivestax.active.life.canada.constant.Endpoints;
import io.reactivestax.active.life.canada.constant.ExceptionHandlerConst;
import io.reactivestax.active.life.canada.constant.TestData;
import io.reactivestax.active.life.canada.entity.FamilyMember;
import io.reactivestax.active.life.canada.enums.PreferredModeOfCommunication;
import io.reactivestax.active.life.canada.exception.InvalidRequestException;
import io.reactivestax.active.life.canada.exception.SomethingWentWrongException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
class EmsServiceTest {

    @Autowired
    private EmsService emsService;

    @MockitoBean
    private RestTemplate restTemplate;

    private FamilyMember familyMember;

    @BeforeEach
    void setUp() {
        familyMember = FamilyMember.builder()
                .familyMemberId(TestData.FAMILY_MEMBER_ID_UUID)
                .preferredModeOfCommunication(PreferredModeOfCommunication.EMAIL)
                .emailId(TestData.EMAIL)
                .businessPhone(TestData.PHONE)
                .build();
    }

    @Test
    void testSendToEms_Success_EMAIL() {
        when(restTemplate.exchange(anyString(), any(HttpMethod.class), any(HttpEntity.class), eq(String.class)))
                .thenReturn(new ResponseEntity<>(HttpStatus.OK));

        assertDoesNotThrow(() -> emsService.sendToEms(familyMember, TestData.MESSAGE));
    }

    @Test
    void testSendToEms_Failure_5xx_BUSINESS_PHONE() {
        familyMember.setPreferredModeOfCommunication(PreferredModeOfCommunication.BUSINESS_PHONE);
        when(restTemplate.exchange(anyString(), any(HttpMethod.class), any(HttpEntity.class), eq(String.class)))
                .thenReturn(new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR));

        assertDoesNotThrow(() -> emsService.sendToEms(familyMember, TestData.MESSAGE));
    }

    @Test
    void testSendToEms_Failure_4xx_HOME_PHONE() {
        familyMember.setPreferredModeOfCommunication(PreferredModeOfCommunication.HOME_PHONE);
        when(restTemplate.exchange(anyString(), any(HttpMethod.class), any(HttpEntity.class), eq(String.class)))
                .thenReturn(new ResponseEntity<>(HttpStatus.BAD_REQUEST));

        assertDoesNotThrow(() -> emsService.sendToEms(familyMember, TestData.MESSAGE));
    }

    @Test
    void testSendToEmsOtp_Success_EMAIL() {
        when(restTemplate.exchange(anyString(), any(HttpMethod.class), any(HttpEntity.class), eq(String.class)))
                .thenReturn(new ResponseEntity<>(HttpStatus.OK));

        assertDoesNotThrow(() -> emsService.sendToEmsOtp(familyMember));
    }

    @Test
    void testSendToEmsOtp_Failure_5xx_BUSINESS_PHONE() {
        familyMember.setPreferredModeOfCommunication(PreferredModeOfCommunication.BUSINESS_PHONE);
        when(restTemplate.exchange(anyString(), any(HttpMethod.class), any(HttpEntity.class), eq(String.class)))
                .thenReturn(new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR));

        assertDoesNotThrow(() -> emsService.sendToEmsOtp(familyMember));
    }

    @Test
    void testSendToEmsOtp_Failure_4xx_HOME_PHONE() {
        familyMember.setPreferredModeOfCommunication(PreferredModeOfCommunication.HOME_PHONE);
        when(restTemplate.exchange(anyString(), any(HttpMethod.class), any(HttpEntity.class), eq(String.class)))
                .thenReturn(new ResponseEntity<>(HttpStatus.BAD_REQUEST));

        assertDoesNotThrow(() -> emsService.sendToEmsOtp(familyMember));
    }

    @Test
    void testSendToEmsForVerification_Success() {
        when(restTemplate.exchange(anyString(), any(HttpMethod.class), any(HttpEntity.class), eq(String.class)))
                .thenReturn(new ResponseEntity<>(HttpStatus.OK));

        boolean result = emsService.sendToEmsForVerification("12345", "67890");
        assertTrue(result);
    }

    @Test
    void testSendToEmsForVerification_Failure_5xx() {
        when(restTemplate.exchange(anyString(), any(HttpMethod.class), any(HttpEntity.class), eq(String.class)))
                .thenReturn(new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR));

        SomethingWentWrongException exception = assertThrows(SomethingWentWrongException.class,
                () -> emsService.sendToEmsForVerification("12345", "67890"));
        assertEquals(ExceptionHandlerConst.VERIFICATION_FAILED, exception.getMessage());
    }

    @Test
    void testSendToEmsForVerification_Failure_4xx() {
        when(restTemplate.exchange(anyString(), any(HttpMethod.class), any(HttpEntity.class), eq(String.class)))
                .thenReturn(new ResponseEntity<>(HttpStatus.BAD_REQUEST));

        InvalidRequestException exception = assertThrows(InvalidRequestException.class,
                () -> emsService.sendToEmsForVerification("12345", "67890"));
        assertEquals(ExceptionHandlerConst.VERIFICATION_FAILED, exception.getMessage());
    }
}
