package io.reactivestax.active.life.canada.service;

import io.reactivestax.active.life.canada.constant.Endpoints;
import io.reactivestax.active.life.canada.constant.ExceptionHandlerConst;
import io.reactivestax.active.life.canada.constant.Message;
import io.reactivestax.active.life.canada.entity.FamilyMember;
import io.reactivestax.active.life.canada.entity.OfferedCourseWaitlist;
import io.reactivestax.active.life.canada.enums.PreferredModeOfCommunication;
import io.reactivestax.active.life.canada.exception.InvalidRequestException;
import io.reactivestax.active.life.canada.exception.SomethingWentWrongException;
import io.reactivestax.active.life.canada.model.EmsRequest;
import io.reactivestax.active.life.canada.model.EmsVerify;
import io.reactivestax.active.life.canada.repository.OfferedCourseWaitlistRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.text.MessageFormat;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
public class EmsService {

    private final RestTemplate restTemplate;
    private final OfferedCourseWaitlistRepository offeredCourseWaitlistRepository;

    public EmsService(RestTemplate restTemplate,
                      OfferedCourseWaitlistRepository offeredCourseWaitlistRepository) {
        this.restTemplate = restTemplate;
        this.offeredCourseWaitlistRepository = offeredCourseWaitlistRepository;
    }

    public void sendToEms(FamilyMember familyMember, String message) {
        EmsRequest emsRequest = prepareEmsRequest(familyMember, message);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<EmsRequest> request = new HttpEntity<>(emsRequest, headers);
        ResponseEntity<String> response = restTemplate.exchange(getEnsEndpoint(familyMember.getPreferredModeOfCommunication()), HttpMethod.POST, request,
                String.class);
        logResponseFromEms(response.getStatusCode());
        validateResponseCode(response.getStatusCode(), ExceptionHandlerConst.EMS_SEND_REQUEST_FAILED);
    }

    public void sendToEmsOtp(FamilyMember familyMember) {
        EmsRequest emsOtpRequest = prepareEmsRequest(familyMember, "");
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<EmsRequest> request = new HttpEntity<>(emsOtpRequest, headers);
        ResponseEntity<String> response = restTemplate.exchange(getEnsOtpEndpoint(familyMember.getPreferredModeOfCommunication()), HttpMethod.POST, request, String.class);
        logResponseFromEms(response.getStatusCode());
        validateResponseCode(response.getStatusCode(), ExceptionHandlerConst.OTP_SEND_REQUEST_FAILED);
    }

    public boolean sendToEmsForVerification(String memberId, String otp) {
        EmsVerify emsVerify = EmsVerify.builder().customerId(memberId).otp(otp).build();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<EmsVerify> request = new HttpEntity<>(emsVerify, headers);
        ResponseEntity<String> response = restTemplate.exchange(Endpoints.ENS_VERIFY_OTP, HttpMethod.PUT, request, String.class);
        logResponseFromEms(response.getStatusCode());
        return validateResponseCode(response.getStatusCode(), ExceptionHandlerConst.VERIFICATION_FAILED);
    }

    private void logResponseFromEms(HttpStatusCode httpStatusCode) {
        log.info("Response from ems service:{}", httpStatusCode);
    }

    private boolean validateResponseCode(HttpStatusCode statusCode, String message) {
        if (statusCode.is5xxServerError()) {
            throw new SomethingWentWrongException(message);
        } else if (statusCode.is4xxClientError()) {
            throw new InvalidRequestException(message);
        } else return statusCode.is2xxSuccessful();
    }

    private EmsRequest prepareEmsRequest(FamilyMember familyMember, String message) {
        EmsRequest emsRequest = EmsRequest.builder()
                .customerId(familyMember.getFamilyMemberId().toString())
                .message(message)
                .build();
        switch (familyMember.getPreferredModeOfCommunication()) {
            case HOME_PHONE -> emsRequest.setPhoneNumber(familyMember.getHomePhone());
            case BUSINESS_PHONE -> emsRequest.setPhoneNumber(familyMember.getBusinessPhone());
            case EMAIL -> emsRequest.setEmail(familyMember.getEmailId());
        }

        return emsRequest;
    }

    private String getEnsEndpoint(PreferredModeOfCommunication preferredModeOfCommunication) {
        if (preferredModeOfCommunication.equals(PreferredModeOfCommunication.EMAIL)) return Endpoints.ENS_EMAIL;
        return Endpoints.ENS_SMS;
    }

    private String getEnsOtpEndpoint(PreferredModeOfCommunication preferredModeOfCommunication) {
        if (preferredModeOfCommunication.equals(PreferredModeOfCommunication.EMAIL)) return Endpoints.ENS_EMAIL_OTP;
        return Endpoints.ENS_SMS_OTP;
    }

    public void sendEmsNotificationToAllWaitlistedMembersByOfferedCourseId(UUID offeredCourseId, String courseName) {
        List<FamilyMember> allTheWaitlistedMembersByOfferedCourseId = offeredCourseWaitlistRepository
                .findAllByOfferedCourse_OfferedCourseId(offeredCourseId).stream().map(OfferedCourseWaitlist::getFamilyMember)
                .toList();
        allTheWaitlistedMembersByOfferedCourseId.forEach(familyMember -> sendToEms(familyMember,
                MessageFormat.format(Message.SPOT_AVAILABLE_FOR_ENROLLMENT, familyMember.getName(), courseName)));
    }
}
