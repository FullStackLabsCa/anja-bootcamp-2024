package io.reactivestax.active.life.canada.service;

import io.reactivestax.active.life.canada.constant.Endpoints;
import io.reactivestax.active.life.canada.constant.ExceptionMessage;
import io.reactivestax.active.life.canada.constant.Message;
import io.reactivestax.active.life.canada.entity.FamilyMember;
import io.reactivestax.active.life.canada.enums.PreferredModeOfCommunication;
import io.reactivestax.active.life.canada.exception.InvalidRequestException;
import io.reactivestax.active.life.canada.exception.SomethingWentWrongException;
import io.reactivestax.active.life.canada.model.EmsRequest;
import io.reactivestax.active.life.canada.model.EmsVerify;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.text.MessageFormat;

@Slf4j
@Service
public class EmsService {

    private final RestTemplate restTemplate;

    public EmsService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public void sendToEms(FamilyMember familyMember) {
        String activationLink = MessageFormat.format(Endpoints.ACTIVATION_LINK_URL, familyMember.getActivationToken());
        EmsRequest emsRequest = EmsRequest.builder().customerId(familyMember.getFamilyMemberId().toString()).phoneNumber(familyMember.getHomePhone()).message(MessageFormat.format(Message.ACTIVATION_LINK_MESSAGE, familyMember.getName(), activationLink)).build();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<EmsRequest> request = new HttpEntity<>(emsRequest, headers);
        log.info(request.getBody().toString());
        ResponseEntity<String> response = restTemplate.exchange(getEnsEndpoint(familyMember.getPreferredModeOfCommunication()), HttpMethod.POST, request,
                String.class);

        log.info("Response: " + response.getStatusCode());
        validateResponseCode(response.getStatusCode());
    }

    public void sendToEmsOtp(FamilyMember familyMember) {
        EmsRequest emsOtpRequest = EmsRequest.builder().customerId(familyMember.getFamilyMemberId().toString()).phoneNumber(familyMember.getHomePhone()).build();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<EmsRequest> request = new HttpEntity<>(emsOtpRequest, headers);
        log.info(request.getBody().toString());
        ResponseEntity<String> response = restTemplate.exchange(getEnsOtpEndpoint(familyMember.getPreferredModeOfCommunication()), HttpMethod.POST, request, String.class);

        log.info("Response: " + response.getStatusCode());
        validateResponseCode(response.getStatusCode());
    }

    public void sendToEmsForVerification(String memberId, String otp) {
        EmsVerify emsVerify = EmsVerify.builder().customerId(memberId).otp(otp).build();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<EmsVerify> request = new HttpEntity<>(emsVerify, headers);
        log.info(request.getBody().toString());
        ResponseEntity<String> response = restTemplate.exchange(Endpoints.ENS_VERIFY_OTP, HttpMethod.PUT, request, String.class);
        log.info("Response: " + response.getStatusCode());
        validateResponseCode(response.getStatusCode());
    }

    private void validateResponseCode(HttpStatusCode statusCode) {
        if (statusCode.is5xxServerError()) {
            throw new SomethingWentWrongException(ExceptionMessage.INTERNAL_ERROR);
        } else if (statusCode.is4xxClientError()) {
            throw new InvalidRequestException(ExceptionMessage.BAD_REQUEST);
        }
    }

    public String getEnsEndpoint(PreferredModeOfCommunication preferredModeOfCommunication) {
        if (preferredModeOfCommunication.equals(PreferredModeOfCommunication.EMAIL)) return Endpoints.ENS_EMAIL;
        return Endpoints.ENS_SMS;
    }

    public String getEnsOtpEndpoint(PreferredModeOfCommunication preferredModeOfCommunication) {
        if (preferredModeOfCommunication.equals(PreferredModeOfCommunication.EMAIL)) return Endpoints.ENS_EMAIL_OTP;
        return Endpoints.ENS_SMS_OTP;
    }
}
