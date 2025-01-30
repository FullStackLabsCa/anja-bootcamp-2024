package io.reactivestax.active.life.canada.service;

import io.reactivestax.active.life.canada.constant.Endpoints;
import io.reactivestax.active.life.canada.constant.Message;
import io.reactivestax.active.life.canada.entity.FamilyMember;
import io.reactivestax.active.life.canada.model.EmsRequest;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.text.MessageFormat;
import java.util.UUID;

@Service
public class EmsService {

    private final RestTemplate restTemplate;

    public EmsService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public void sendToEms(FamilyMember familyMember) {
        String activationLink = MessageFormat.format(Endpoints.ACTIVATION_LINK_URL, familyMember.getActivationToken());
        EmsRequest emsRequest = EmsRequest.builder()
                .customerId(familyMember.getFamilyMemberId().toString())
                .phoneNumber(familyMember.getHomePhone())
                .message(MessageFormat.format(Message.ACTIVATION_LINK_MESSAGE, familyMember.getName(), activationLink))
                .build();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<EmsRequest> request = new HttpEntity<>(emsRequest, headers);
        System.out.println(request.getBody());
//        ResponseEntity<String> response = restTemplate.exchange(Endpoints.ENS_SMS, HttpMethod.POST, request, String.class);

//        System.out.println("Response: " + response.getStatusCode());
    }

    public void sendToEmsOtp(FamilyMember familyMember) {
        EmsRequest emsOtpRequest = EmsRequest.builder()
                .customerId(familyMember.getFamilyMemberId().toString())
                .phoneNumber(familyMember.getHomePhone())
                .build();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<EmsRequest> request = new HttpEntity<>(emsOtpRequest, headers);
        System.out.println(request.getBody());
//        ResponseEntity<String> response = restTemplate.exchange(Endpoints.ENS_SMS_OTP, HttpMethod.POST, request, String.class);

//        System.out.println("Response: " + response.getStatusCode());
    }
}
