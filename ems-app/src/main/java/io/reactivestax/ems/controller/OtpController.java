package io.reactivestax.ems.controller;

import io.reactivestax.ems.constant.SuccessMessage;
import io.reactivestax.ems.dto.BaseDTO;
import io.reactivestax.ems.dto.SuccessfulResponse;
import io.reactivestax.ems.dto.ValidatedOtpDTO;
import io.reactivestax.ems.dto.VerifyOtpDTO;
import io.reactivestax.ems.enums.NotificationMethod;
import io.reactivestax.ems.service.OtpService;
import io.reactivestax.ems.validation.CallGroup;
import io.reactivestax.ems.validation.EmailGroup;
import io.reactivestax.ems.validation.SmsGroup;
import jakarta.validation.groups.Default;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/otp")
public class OtpController {

    private final OtpService otpService;

    @Autowired
    public OtpController(OtpService otpService) {
        this.otpService = otpService;
    }

    @PostMapping(value = "/sms", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<SuccessfulResponse> smsOtp(@Validated({SmsGroup.class, Default.class}) @RequestBody BaseDTO otpDTO) {
        this.otpService.save(otpDTO, NotificationMethod.SMS);

        return ResponseEntity.ok(SuccessfulResponse.builder().message(SuccessMessage.SUCCESS_OTP_MESSAGE).build());
    }

    @PostMapping(value = "/call", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<SuccessfulResponse> callOtp(@Validated({CallGroup.class, Default.class}) @RequestBody BaseDTO otpDTO) {
        this.otpService.save(otpDTO, NotificationMethod.CALL);

        return ResponseEntity.ok(SuccessfulResponse.builder().message(SuccessMessage.SUCCESS_OTP_MESSAGE).build());
    }

    @PostMapping(value = "/email", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<SuccessfulResponse> emailOtp(@Validated({EmailGroup.class, Default.class}) @RequestBody BaseDTO otpDTO) {
        this.otpService.save(otpDTO, NotificationMethod.EMAIL);

        return ResponseEntity.ok(SuccessfulResponse.builder().message(SuccessMessage.SUCCESS_OTP_MESSAGE).build());
    }

    @PutMapping(value = "/verify", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<SuccessfulResponse> verifyOtp(@RequestBody VerifyOtpDTO verifyOtpDTO) {
        this.otpService.verifyOtp(verifyOtpDTO);

        return ResponseEntity.ok(SuccessfulResponse.builder().message(SuccessMessage.SUCCESS_OTP_VERIFICATION).build());
    }

    @GetMapping(value = "/status/{customerId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ValidatedOtpDTO> statusOtp(@PathVariable String customerId) {
        ValidatedOtpDTO validatedOtpDTO = this.otpService.status(customerId);

        return ResponseEntity.ok(validatedOtpDTO);
    }
}
