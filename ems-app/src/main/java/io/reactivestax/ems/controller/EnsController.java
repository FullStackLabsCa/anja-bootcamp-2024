package io.reactivestax.ems.controller;

import io.reactivestax.ems.constant.Endpoints;
import io.reactivestax.ems.constant.SuccessMessage;
import io.reactivestax.ems.dto.BaseDTO;
import io.reactivestax.ems.dto.SuccessfulResponse;
import io.reactivestax.ems.enums.NotificationMethod;
import io.reactivestax.ems.service.EnsService;
import io.reactivestax.ems.validation.CallGroup;
import io.reactivestax.ems.validation.EmailGroup;
import io.reactivestax.ems.validation.MessageGroup;
import io.reactivestax.ems.validation.SmsGroup;
import jakarta.validation.groups.Default;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(Endpoints.ENS_BASE)
@Validated
public class EnsController {

    private final EnsService ensService;

    @Autowired
    public EnsController(EnsService ensService) {
        this.ensService = ensService;
    }

    @PostMapping(value = Endpoints.SMS, produces = MediaType.APPLICATION_JSON_VALUE, consumes =
            MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAuthority('SCOPE_ems.sms')")
    public ResponseEntity<SuccessfulResponse> sendSms(@Validated({SmsGroup.class, MessageGroup.class, Default.class}) @RequestBody BaseDTO messageDTO) {
        this.ensService.save(messageDTO, NotificationMethod.SMS);

        return ResponseEntity.ok(SuccessfulResponse.builder().message(SuccessMessage.SUCCESS_ENS_MESSAGE).build());
    }

    @PostMapping(value = Endpoints.CALL, produces = MediaType.APPLICATION_JSON_VALUE, consumes =
            MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAuthority('SCOPE_ems.call')")
    public ResponseEntity<SuccessfulResponse> call(@Validated({CallGroup.class, MessageGroup.class, Default.class}) @RequestBody BaseDTO messageDTO) {
        this.ensService.save(messageDTO, NotificationMethod.CALL);

        return ResponseEntity.ok(SuccessfulResponse.builder().message(SuccessMessage.SUCCESS_ENS_MESSAGE).build());
    }

    @PostMapping(value = Endpoints.EMAIL, produces = MediaType.APPLICATION_JSON_VALUE, consumes =
            MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAuthority('SCOPE_ems.email')")
    public ResponseEntity<SuccessfulResponse> email(@Validated({EmailGroup.class, MessageGroup.class, Default.class}) @RequestBody BaseDTO messageDTO) {
        this.ensService.save(messageDTO, NotificationMethod.EMAIL);

        return ResponseEntity.ok(SuccessfulResponse.builder().message(SuccessMessage.SUCCESS_ENS_MESSAGE).build());
    }
}
