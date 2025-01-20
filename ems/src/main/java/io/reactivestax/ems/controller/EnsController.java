package io.reactivestax.ems.controller;

import io.reactivestax.ems.constant.SuccessMessage;
import io.reactivestax.ems.dto.MessageDTO;
import io.reactivestax.ems.dto.SuccessfulResponse;
import io.reactivestax.ems.enums.NotificationMethod;
import io.reactivestax.ems.service.EnsService;
import io.reactivestax.ems.validation.SmsGroup;
import jakarta.validation.groups.Default;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/ems")
@Validated
public class EnsController {

    private final EnsService ensService;

    @Autowired
    public EnsController(EnsService ensService) {
        this.ensService = ensService;
    }

    @PostMapping(value = "/sms", produces = "application/json", consumes = "application/json")
    public ResponseEntity<SuccessfulResponse> sendSms(@Validated({SmsGroup.class, Default.class}) @RequestBody MessageDTO messageDTO) {
        this.ensService.save(messageDTO, NotificationMethod.SMS);

        return ResponseEntity.ok(new SuccessfulResponse(SuccessMessage.SUCCESS_MESSAGE));
    }
}
